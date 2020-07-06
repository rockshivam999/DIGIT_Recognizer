package com.vssqure.recogniser

import android.content.Intent
import android.content.res.AssetManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.stephenvinouze.drawingview.DrawingView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class MainActivity : AppCompatActivity() {
   lateinit var  magic:Button
   lateinit var  clear:Button
   lateinit var  exit:Button
   lateinit var data:DrawingView
    lateinit var preview:ImageView
     var count:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        magic = findViewById<Button>(R.id.Magic)
        exit = findViewById<Button>(R.id.exit)
        clear = findViewById<Button>(R.id.clear)
        data=findViewById<DrawingView>(R.id.drawing_view)
        preview= findViewById<ImageView>(R.id.preview)


       exit.setOnClickListener(){
            finishAffinity()
        }
        clear.setOnClickListener(){
            data.resetDrawing()
        }




        magic.setOnClickListener(){
            var drawingBitmap: Bitmap = data.getDrawing()
            var scaled:Bitmap= Bitmap.createScaledBitmap(drawingBitmap,28,28,true)
            //scaled=toGrayscale(scaled)
            preview.setImageBitmap(scaled)
            var inputdata:ByteBuffer=convertBitmapToByteBuffer(scaled)

            //var outputs:FloatArray= floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
            val options = Interpreter.Options()
            options.setNumThreads(5)
            options.setUseNNAPI(true)

            var interpreter:Interpreter = Interpreter(loadModelFile(assets, "mnist.tflite"), options)
            val result = Array(1) { FloatArray(10) }

            interpreter.run(inputdata , result)
            var oneD:FloatArray=result[0]
            var intent:Intent= Intent(this,output::class.java)
            intent.putExtra("output",oneD)
            startActivity(intent)



        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap):ByteBuffer {
       var mImageData:ByteBuffer = ByteBuffer.allocateDirect(
            4  * 28* 28 );
        mImageData.order(ByteOrder.nativeOrder());
        val mImagePixels = IntArray(28*28)

        //mImageData.rewind()
        bitmap.getPixels(
            mImagePixels, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )
        var pixel = 0
        for (i in 0 until 28 ){
            for (j in 0 until 28) {
                val value: Int = mImagePixels[pixel++]
                mImageData.putFloat(convertPixel(value))
            }
        }
        return mImageData
    }
    private fun convertPixel(color: Int): Float {
        count++
       // Log.d("MYTAG",color.toString()+" "+count)
        return if(color==0)
            (0f)
        else
            (1f)

    }
    /*fun bitmapToByteBuffer(bitmap:Bitmap,size:Int): ByteBuffer {

        val bytebuffer= ByteBuffer.allocateDirect(4*size*size)
        bytebuffer.order(ByteOrder.nativeOrder())
        val intarray=IntArray(size*size)
        //bitmap.getPixels(intarray,0,0,bitmap.width,bitmap.height,0,0)
        bitmap.getPixels(intarray, 0,bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel=0
        for (i in 0 until 28){
            for(j in 0 until 28){
                val value=intarray[pixel++]
                if(value>120) {
                    bytebuffer.putFloat(1f)
                }
                else {
                    bytebuffer.putFloat(0f)
                }
            }
        }
        return bytebuffer
    }*/
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


}
