package com.kotlinizer.injection

@Suppress("UNCHECKED_CAST")
class Injector private constructor() {

    /**
     * The Registry of items held by the Dependency Injector.
     */
    private val registry = HashSet<RegistryItem<Any>>()

    /**
     * The Registry of all the classes that Subscribe to an event or class event.
     */
    private val subscriberRegistry = HashSet<RegistryItem<Any>>()

    companion object {

        private var injectorInstance: Injector? = null

        /**
         * Current [Injector] instance, if initialized. If not initialized, this will return a new [Injector] instance.
         */
        @get:Synchronized
        val instance: Injector
            get() = injectorInstance ?: Injector().also { injectorInstance = it }
    }

    /**
     * Registers a dependency into the Dependency Injector.
     *
     * @param type The [Class] of the dependency to store.
     * @param provider The Value [T] to store.
     * @param identifier The [String] Identifier to store, if multiple items of the same type are to be stored.
     * @return The [Boolean] value whether the dependency was successfully stored.
     */
    fun <T> register(type: Class<T>, provider: T, identifier: String? = null): Boolean {
        return registry.add(
            RegistryItem(
                identifier = identifier,
                clazz = type,
                item = provider as Any
            )
        )
    }

    /**
     * Unregisters a dependency from the Dependency Injector.
     *
     * @param type The [Class] of the dependency to remove.
     * @param identifier The [String] Identifier to remove, if multiples are currently stored.
     * @return The [Boolean] value whether the dependency was successfully unregistered.
     */
    fun <T> unregister(type: Class<T>, identifier: String? = null): Boolean {
        registry.first { it.clazz == type && it.identifier == identifier }.let {
            return registry.remove(it)
        }
    }

    /**
     * Resolves/Retrieves a single dependency from the provided criteria.
     *
     * @param type The [Class] of the dependency to return.
     * @param identifier The [String] Identifier to return, if multiples are currently stored.
     * @return The value [T] retrieved from the Dependency Injector.
     */
    fun <T> resolve(type: Class<T>, identifier: String? = null): T? {
        return registry.find { identifier == it.identifier && type == it.clazz }?.item as? T
    }

    /**
     * Resolves/Retrieves all of the dependencies of a provided type.
     *
     * @param type The [Class] type of the dependencies to return.
     * @return A [List] of dependencies, [T], that are of the provided type. Empty List will be returned if no
     * dependencies can be found.
     */
    fun <T> resolveAll(type: Class<T>): List<T> {
        return registry.filter { it.clazz == type }.map { it.item } as? List<T> ?: emptyList()
    }

    /**
     * Publishes data to known subscribers.
     *
     * @param type The [Class] of the data to be published.
     * @param identifier The [String] value of the subscriber to publish to.
     */
    fun <T> publish(type: Class<T>, data: T, identifier: String? = null) {
        subscriberRegistry.filter { it.clazz == type && it.identifier == identifier }.forEach {
            (it.item as Subscriber<T>).process(data = data)
        }
    }

    /**
     * Unloads all the dependencies currently stored in the com.kotlinizer.injection.Injector.
     */
    fun unloadDependencies() {
        registry.clear()
        subscriberRegistry.clear()
    }

    /**
     * Adds a subscriber of a given Class Type.
     *
     * @param type The [Class] of the dependency to return.
     * @param subscriber The [Subscriber] implementation, or `WHO` is subscribing/listening.
     * @param identifier The [String] Identifier to store, if multiple items of the same type are to be stored.
     * @return The [Boolean] value whether the subscriber was successfully added, or not.
     */
    fun <T> addSubscriber(type: Class<T>, subscriber: Subscriber<T>, identifier: String? = null): Boolean {
        return subscriberRegistry.add(
            RegistryItem(
                identifier = identifier,
                clazz = type,
                item = subscriber as Any
            )
        )
    }

    /**
     * Removes a subscriber of a given Class Type.
     *
     * @param type The [Class] of the dependency to return.
     * @param identifier The [String] Identifier to remove, if multiple items of the same type are currently stored.
     * @return The [Boolean] value whether the subscriber was successfully removed, or not.
     */
    fun <T> removeSubscriber(type: Class<T>, identifier: String? = null): Boolean {
        var isRemoved = false
        subscriberRegistry.filter { it.clazz == type && it.identifier == identifier }.forEach {
            isRemoved = subscriberRegistry.remove(it)
        }
        return isRemoved
    }

    private data class RegistryItem<I>(
        val identifier: String?,
        val clazz: Class<*>,
        val item: I
    )
}