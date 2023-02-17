package com.kotlinizer.injection

/**
 * Assists in publishing any object to the dependency injector.
 *
 * @param identifier The [String] value of t he identifier that was used when registering a subscriber.
 */
fun Any.publishToInjector(identifier: String? = null) {
        Injector.instance.publish(type = this.javaClass, data = this, identifier = identifier)
}

/**
 * Assists in registering any object to the dependency injector.
 *
 * @param identifier The [String] value of the identifier used to link to this referenced object, if provided.
 */
fun Any.registerToInjector(identifier: String? = null) {
    Injector.instance.register(this.javaClass, this, identifier)
}