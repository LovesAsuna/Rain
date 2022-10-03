package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.MField
import org.objectweb.asm.tree.FieldNode

class ASMField<T>(name: String, fieldType: Class<T>) : MField<T>(name, fieldType), ASMAnnotationAble {

    var initValue: Any? = null
        private set

    fun build(): FieldNode =
        FieldNode(
            countAccess(this.access, this.static, this.final),
            name,
            fieldType.descriptor,
            null,
            initValue
        )

}