
## 1. Program Structure
-  Program structure is standard structure consists of  </br>
	a. Controller </br>
	b. Service </br>
	c. Repository (In Memory) </br>

## 2. How to run the Program
-  Build the project with mvn clean package. </br>
-  Transaction-service runs in 7777 port. </br>

## 3. Time Complexity

-  The time complexity of retrieve all the children and their children until the last level for a particular transaction is O(N).
-  While, recording the transactions, the index "PARENT_CHILD_TRANSACTION_SECONDARY_MAP" builds the relationship between parent transaction and the   child transaction. Since, the value field is a collection of set, finding a particular transaction is O(1). 
-  First, the parent transaction is retrieved, then using Depth First Search (DFS) the linked child is retrieved from the index. For DFS here, stack is used.
-  Basically PARENT_CHILD_TRANSACTION_SECONDARY_MAP is a LONG to Set of transaction map. Since, Transaction are checked based on their Id. set of transaction only contains the unique transactions.
-  If some new transaction recorded with same id, then it selects the old transaction with the id, removes the old transaction and inserts new transaction.

## 4. Technology:
-  Used Spring boot 2 for developing the services.</br>
-  Swagger for documentation and testing of the API.</br>
-  For In-Memory storing, used MAP.


