import com.kotlinizer.injection.Injector
import com.kotlinizer.injection.Subscriber
import com.kotlinizer.injection.publishToInjector
import com.kotlinizer.injection.registerToInjector
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class InjectorTest {

    private val injector = Injector.instance
    private val testName = Name(
        firstName = FIRST_NAME,
        lastName = LAST_NAME,
        phoneNumber = PHONE_NUMBER
    )

    companion object {
        private const val OWNER_NAME = "owner_name"
        private const val FIRST_NAME = "kotlinizer"
        private const val LAST_NAME = "kotlin-ftw"
        private const val PHONE_NUMBER = 5555555555L
    }

    @BeforeEach
    fun setup() {
        // Given an empty Injector
        injector.unloadDependencies()

        // Given an object registered into the dependency injector.
        injector.register(
            type = Name::class.java,
            provider = testName,
            identifier = OWNER_NAME
        )
    }

    @AfterEach
    fun tearDown() {
        injector.unloadDependencies()
    }

    @Test
    fun testInitialization() {
        // Given an com.kotlinizer.injection.Injector Instance.
        assertNotNull(injector)

        // Given an object from an com.kotlinizer.injection.Injector.
        val testName = injector.resolve(Name::class.java, OWNER_NAME)

        // Assert the sample object is not null.
        assertNotNull(testName)

        // Assert the Content Matches.
        assertEquals(FIRST_NAME, testName.firstName, "First Name did not match.")
        assertEquals(LAST_NAME, testName.lastName, "Last Name did not match.")
        assertEquals(PHONE_NUMBER, testName.phoneNumber, "Phone Number did not match.")

        // Ensure reference is equal to original object.
        assertSame(testName, this.testName)
    }

    @Test
    fun testUnregisterDependency() {
        // Given an com.kotlinizer.injection.Injector Instance.
        assertNotNull(injector)

        // Given an object from an com.kotlinizer.injection.Injector.
        val testName = injector.resolve(Name::class.java, OWNER_NAME)

        // Assert the sample object is not null.
        assertNotNull(testName)

        // Ensure the object was successfully unregistered from the dependency injector.
        assertTrue(injector.unregister(Name::class.java, OWNER_NAME))

        // Re-resolve the dependency to ensure not present.
        val unregistered = injector.resolve(Name::class.java, OWNER_NAME)

        // Assert this object is now null after unregistering.
        assertNull(unregistered, "Resolved dependency is not null.")
    }

    @Test
    fun testExtensionFunction() {
        // Given an com.kotlinizer.injection.Injector Instance.
        assertNotNull(injector)

        val name = Name(
            firstName = "Mike",
            lastName = "Reynolds",
            phoneNumber = PHONE_NUMBER
        )

        name.registerToInjector(FIRST_NAME + LAST_NAME)

        val resolvedDependency = injector.resolve(Name::class.java, FIRST_NAME + LAST_NAME)

        assertNotNull(resolvedDependency, "Dependency was unexpectedly null.")

        assertSame(name, resolvedDependency, "Object was not the same.")

        name.publishToInjector()
    }

    @Test
    fun testPublisher() {
        // Given an com.kotlinizer.injection.Injector Instance.
        assertNotNull(injector)

        injector.addSubscriber(Name::class.java, TestSubscriber())
        injector.addSubscriber(Long::class.java, TestSubscriber2())

        injector.publish(Name::class.java, testName)
    }

    @Test
    fun testResolvingMultiple() {
        Name("Kotlin", "Multiple-Test", 398478234L).apply {
            registerToInjector("testtest")
        }

        // Given additional name registered.
        val names = injector.resolveAll(Name::class.java)

        // Assert both names were resolved.
        assertTrue(names.size == 2, "Incorrect amount of Names Resolved.")

        // Then remove the last added Name.
        injector.unregister(Name::class.java, "testtest")

        // Finally, assert the last name was removed.
        val names2 = injector.resolveAll(Name::class.java)

        // Assert there is only one Name left.
        assertTrue(names2.size == 1, "Incorrect amount of Names Resolved.")
    }

    private data class Name(
        val firstName: String,
        val lastName: String,
        val phoneNumber: Long
    )

    private class TestSubscriber : Subscriber<Name> {
        override fun process(data: Name) {
            assertIs<Name>(value = data, message = "Item not instance of Name.")
        }
    }

    private class TestSubscriber2 : Subscriber<Long> {
        override fun process(data: Long) {
            assertIs<Long>(value = data, message = "Item not instance of Long.")
        }
    }
}