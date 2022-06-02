package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.*
import javax.inject.Inject
import javax.inject.Named

interface BeanInjector<T> {
    operator fun invoke(bean: T): T
}

class ReflectBeanInject<T>(
    val context: ContextImpl,
    val clazz: Class<T>
) : BeanInjector<T> {

    private val fields =
        clazz.declaredFields
            .filter { !it.isStatic && it.hasAnnotation<Inject>() }
            .mutableMap {
                val named = it.annotation<Named>()
                val setMethod =
                    kotlin.runCatching {
                        clazz.getMethod("set${it.name.toUpperCaseFirstOne()}", it.type)
                    }.getOrNull()

                val dataReader = context.getDataReader(it.genericType)

                if (named == null) {
                    if (setMethod == null) {
                        { instance: T ->
                            if (!it.isAccessible) it.isAccessible = true
                            it.set(instance, dataReader())
                        }
                    } else {
                        { instance: T ->
                            setMethod.invoke(instance, dataReader())
                        }
                    }
                } else {
                    val name = named.value
                    if (setMethod == null) {
                        { instance: T ->
                            if (!it.isAccessible) it.isAccessible = true
                            it.set(instance, dataReader(name))
                        }
                    } else {
                        { instance: T ->
                            setMethod.invoke(instance, dataReader(name))
                        }
                    }
                }
            }.apply {
                addAll(
                    clazz.methods
                        .filter { it.name.startsWith("set") && it.hasAnnotation<Inject>() && it.parameters.size == 1 && it.isExecutable }
                        .mapMap {
                            val named = it.annotation<Named>()
                            val dataReader = context.getDataReader(it.parameters[0].parameterizedType)

                            if (named == null) {
                                { instance: T ->
                                    it.invoke(instance, dataReader())
                                }
                            } else {
                                { instance: T ->
                                    it.invoke(instance, dataReader(named.value))
                                }
                            }
                        }
                )
            }

    override fun invoke(bean: T): T {
        fields.forEach { it(bean) }
        return bean
    }

}
