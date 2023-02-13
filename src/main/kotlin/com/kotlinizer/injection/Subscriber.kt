package com.kotlinizer.injection

/**
 * Interface for a com.kotlinizer.injection.Subscriber to listen for published objects.
 */
interface Subscriber<T> {

    /**
     * Function to process an object being published.
     *
     * @param data The data of a Published object to be processed.
     */
    fun process(data: T)
}