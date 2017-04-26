package example

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// to compile under mainstream kotlinc
// comment out StatelessProperty annotation here 
// and it's usage below for properties classes
import kotlin.properties.StatelessProperty

/**
  Just an example of property delegate in different forms to
  check PoC correctness
*/
class Foo {

    var value: Int = 0

    val x:Int by int()

    val y:Int by DoubleProperty()

    var z:Int by DelegatedDoubleProperty()


    companion object {
        fun int(): DoubleProperty = DoubleProperty()
    }

    override fun toString(): String {
        return "foo value:$value x:$x y:$y z:$z"
    }
}

fun main(args: Array<String>){
    var obj = Foo()
    obj.value = 1
    println(obj)

    obj = Foo()
    obj.value = 2
    println(obj)

    obj = Foo()
    obj.value = 3
    println(obj)

    obj.z = 2 * 6
    println(obj)
}

@StatelessProperty 
open class DoubleProperty: ReadWriteProperty<Foo, Int> {
    init {
        println("created: " + this)
    }
    override operator fun getValue(thisRef: Foo, property: KProperty<*>): Int { return 2 * thisRef.value}
    override operator fun setValue(thisRef: Foo, property: KProperty<*>, value: Int) {
        thisRef.value = value/2
    }
}

@StatelessProperty
class DelegatedDoubleProperty : DoubleProperty {

    val name: String

    private constructor(name: String){
        this.name = name
        println("delegate created: " + this)
    }
    constructor() {
        name = "_FACTORY_INSTANCE"
        println("delegate created: " + this)
    }

    operator fun provideDelegate(
            thisRef: Foo?,
            prop: KProperty<*>
    ): DelegatedDoubleProperty {
        return DelegatedDoubleProperty(prop.name)
    }

    override fun toString(): String {
        return "DelegatedDoubleProperty(name='$name')"
    }

}

