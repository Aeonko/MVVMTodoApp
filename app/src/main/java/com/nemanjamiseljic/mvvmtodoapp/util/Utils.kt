package com.nemanjamiseljic.mvvmtodoapp.util
/**Returns same var as passed but with this it can be marked exchaustive
 * ...because of this when method can check for error's at compile time**/
val <T> T.exchaustive: T
    get() = this