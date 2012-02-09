import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

// ranges is lists
assert (1..3) == [1,2,3]
assert (1..<3) == [1,2]
assert (1..3) instanceof IntRange // groovy.lang
assert (1..3) instanceof List
// notation, lists
def emptyList = []
assert [] instanceof ArrayList
def list = [1,2,3]
assert list[1..-1] == [2,3]
assert list[-3..-2] == [1,2]
assert list[2..1] == [3,2]
// notation, maps
def emptyMap = []
assert [:] instanceof LinkedHashMap
assert [a:1, b:2] == ['a':1, 'b':2]
def method(Map map) {}
method(a:1, b:2)

// syntactic sugar, operators
assert [1,2] << 3 == [1,2,3]    // leftShift()
assert [1,2]+3 == [1,2,3]       // plus()
assert [1,2]+[3,4] == [1,2,3,4]
assert [1,2,3]-[3,4] == [1,2]   // minus
// syntactic sugar, spread
assert ["a","b","1"]*.isNumber() == [false,false,true]
assert ["a","b","1"].number == [false,false,true]


// each
[1,2,3].each { println it }
[1,2,3].each { item -> println item }
[1,2,3].eachWithIndex { item, i -> println "index ${i} contains ${item}" }
[1,2,3].reverseEach { println it }


// filters (find*)
assert [1,2,3].findAll { it % 2 } == [1,3]
assert [1,2,3].find { it % 2 } == 1
assert [1,2,3].findIndexValues { it %2 } == [0,2]
assert [1,2,3].findResults { it % 2 ? it*it : null } == [1,9]
assert [1,2,3].findResult { it % 2 ? it*it : null } == 1

// collect
assert [1,2,3].collect { it * it } == [1,4,9]
assert [1,[2,3],4].collectNested { it*it } == [1,[4,9],16]
assert [1,2,3].collectEntries { ['k' +it, it] } == [k1:1,k2:2,k3:3]
// (also collectMany, collectNested, but those are different beasts)

// types of collections
assert [1,2,3].collect { it } == [1,2,3]              // list
assert [a:1,b:2].collect { key, val -> val } == [1,2] // map
assert (1..3).collect { it } == [1,2,3]               // range
assert [1,2,3].toArray().collect { it } == [1,2,3]    // array
assert "a string".collect { it } == ['a',' ','s','t','r','i','n','g']  // string
assert 42.collect { it } == [42]                      // Any Object
def matcher = ("cheese please" =~ /([^e]+)e+/)        // regexp
assert matcher.collect { it } == [["chee", "ch"], ["se", "s"], [" ple", " pl"], ["ase", "as"]]


// reducers, basic
assert [1,2,3].max() == 3
assert [1,2,3].min() == 1
assert [1,2,3].sum() == 6
// reducers, basic with transform
assert [1,2,3].max{ -it } == 1
assert [1,2,3].max{ a,b-> b<=>a } == 1
// reducers, boolean
assert [1,2,3].any{ it > 3 } == false
assert [1,2,3].every{ it < 4 } == true
// reducers, join
assert [1,2,3].join(";") == "1;2;3"
// reducers, inject
assert [1,2,3].inject(0)   { acc, val -> acc+val } == 6
assert [1,2,3].inject("0") { acc, val -> acc+val } == "0123"
assert [1,2,3].inject([])  { acc, val -> acc+val } == [1,2,3]
// reducers, inject, map version
assert [a:1,b:2].inject(0) { acc, key, val -> acc+val } == 3
// Like reduce better?
Object.metaClass.reduce = {initial, closure->
    delegate.inject(initial, closure)
}
assert [1,2,3].reduce(0){acc,val->acc+val} == 6


// sample data
@EqualsAndHashCode @ToString class Person { def name, address, pets }
@EqualsAndHashCode @ToString class Address { def street, city }
enum Pet { CAT, DOG, BIRD, DUCK }
import static Pet.*
def persons = [
    new Person(name:"Ole",
               address:new Address(street:"Blindvn 1", city:"Oslo"),
               pets:[BIRD, CAT]),
    new Person(name:"Dole",
               address:new Address(street:"Blindvn 2", city:"Oslo"),
               pets:[DOG, CAT]),
    new Person(name:"Doff",
               address:new Address(street:"Strandvn 9", city:"Bergen"),
               pets:[BIRD, DOG]),
    ]
println persons.pets
assert persons.pets == [ [BIRD, CAT], [DOG, CAT], [BIRD, DOG]] 
assert persons.pets.flatten().unique() == [BIRD, CAT, DOG]
assert persons.address.city.unique() == ["Oslo", "Bergen"]



// Set operations
assert [1,2]+3 == [1,2,3]
assert [1,2]+[3,4] == [1,2,3,4]
assert [1,2].plus([3,4]) == [1,2,3,4]

assert [1,2,3]-3 == [1,2]
assert [1,2,3]-[2,3] == [1]
assert [1,2,3].minus([2,3]) == [1]

assert [1,2,3].intersect([3,4]) == [3]
assert [1,2,3].disjoint([4,5]) == true

assert [1,2,3].subsequences() == [[3], [1, 2, 3], [1], [2], [2, 3], [1, 3], [1, 2]] as Set
assert [1,2,3].permutations() == [[1, 2, 3], [2, 3, 1], [3, 2, 1], [3, 1, 2], [2, 1, 3], [1, 3, 2]] as Set
assert [[1,2],[3,4]].combinations() == [[1, 3], [2, 3], [1, 4], [2, 4]]


// Nested collections
assert [1,[2,3],4].flatten() == [1,2,3,4]
assert [['a','b'], [1,2]].transpose() == [['a',1],['b',2]]
assert [1,[2,3],4].collectNested { it+10 } == [11,[12,13],14]


// Other, possibly useful methods
assert [1,2,3].collectMany { [it**2, it**3] } == [1,1,4,8,9,27]

// grep
assert [1,2,3].grep {it < 3} == [1,2]   // identical to findAll
assert [1,2,'a','b'].grep(Integer) == [1,2]
assert [1,2,'a','b'].grep(~/\d/) == [1,2]

// findResults
assert [1,2,3].findResults { it > 1 ? it*it : null } == [4,9]
[1,2,3].findAll { it > 1 }
       .collect { it*it }

       
assert [1,2,3].groupBy { it < 2 ? 'small' : 'big' } == [small:[1], big:[2,3]]
assert [1,2,3].countBy { it < 2 ? 'small' : 'big' } == [small:1,   big:2]


// stack
list = [1,2,3]
assert list.push(4) == true // always true
assert list == [1,2,3,4]
assert list.pop() == 4
assert list == [1,2,3]

// functional style
assert [1,2,3].head() == 1
assert [1,2,3].tail() == [2,3]
assert [1,2,3].first() == 1
assert [1,2,3].last() == 3
assert [1,2,3].take(2) == [1,2]
assert [1,2,3].drop(2) == [3]
assert [1,2,3].split { it<3 } == [[1,2],[3]]

// map.withDefault()
def newMap = [:].withDefault { [] }
[a:1,b:2,c:2].each { key, val ->
    newMap[val] << key
}
assert newMap == [1:['a'], 2:['b','c']]


// Other
//transpose
//split