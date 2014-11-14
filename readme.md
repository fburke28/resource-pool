Design:
 - Coding against an interface (ResourcePool) to allow for difference implementations as necessary.
 - The open boolean value is volatile so that all threads know of any changes to its value.
 - Available and acquired resources are tracked in separate data structures - ConcurrentLinkedQueue.
 - ConcurrentLinkedQueue provides an unbounded data structure so the managed resources can grow as needed.
 - ConcurrentLinkedQueue provides for efficient multi-threaded access. Queue data structure provides efficient poll and offer operations to quickly add/acquire resources.
 - To protect against thread collisions a fair ReentrantReadWriteLock is used. Fair means that waiting threads are prioritized based on the time they were waiting.
 - Heavier operations like remove and close use an exclusive write lock. In the case of a waiting remove it has to release the write lock for a period of time while blocking on acquired resources.
   The acquired resource can then take a read for release purposes which then allows remove to progress and re-acquire the write lock to finish the remove operation.
 - Optimized operations like add, release and acquire use a read lock. Multiple threads can hold a read lock.

Resource Pool Operations:
 - Add: Read lock needed just to ensure we don't add duplicates or collide with a remove operation.
 - Release: Read lock needed - remove from acquiredResource pool and add back to available resources.
 - Acquire: Read lock needed - take an available resource and make it an acquired resource.
 - Remove: Remove resource from resource pool. Write lock needed to perform remove of resource.
 - Close: Close the pool by setting the open property to false. Write lock needed once acquired resources is empty to perform this operation. CloseNow does not wait for the acquired resources to be empty.

Test:
MainDriver.java - runs the integration test which performs a few basic operations and also launches two threads executing a few competing operations - i.e. remove while resource is acquired.
Log4j output is logged to the console
AddAndAcquireActivity.java - add, acquire operations.
RemoveActivity.java - remove operations.

Improvements:
1.	Complete unit tests. Unit tests are useful to cover all code branches decoupled from any external dependencies. Using Spring dependency injection 
	helps here as we are injecting in an implementation of the ResourcePool interface. More complex testing can use mocking frameworks here such as Mockito or PowerMock.
2.	Evaluate different data structures based on testing performance metrics.
3.	Optimize release operation - evaluate usage of remove and offer operations.
4.	Better define what close means! Does it mean we should empty the current resources in the pool as well. Is a write lock completely necessary for the close operation.
	We may need to stop additional acquires once close is called however release also needs a read lock. Possibly add more locks and more complexity here.
5.	Better define what can be done while the resource pool is not open.
6.	Mavenize project - pull in Spring and log4j dependencies through maven pom.xml