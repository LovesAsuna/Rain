package com.IceCreamQAQ.Yu.controller.special

import java.lang.RuntimeException

object DoNone
object SkipMe

open class ActionResult(val result: Any) : RuntimeException()
object DoNoneThrowable : ActionResult(DoNone)
object SkipMeThrowable : ActionResult(SkipMe)
