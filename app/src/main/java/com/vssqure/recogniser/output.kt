package com.vssqure.recogniser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class output : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_output)
        var output:TextView=findViewById(R.id.output)
        var actualop:TextView=findViewById(R.id.actualop)
        var out=intent.getFloatArrayExtra("output")
        var max:Float=out[0]
        var maxIndex:Int=0

        for (i in 0..9){
            if(max<out[i]){
                max=out[i]
                maxIndex=i
            }
            output.append(i.toString()+"--->"+out[i])
            output.append("\n")
        }
        actualop.setText(maxIndex.toString() +"--->"+max)
    }
}
