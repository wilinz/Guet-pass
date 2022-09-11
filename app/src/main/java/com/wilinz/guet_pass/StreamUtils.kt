package com.wilinz.guet_pass

import java.io.InputStream
import java.io.OutputStream

fun InputStream.copyToAndClose(out:OutputStream){
    this.use { input->
        out.use { out->
            input.copyTo(out)
        }
    }
}