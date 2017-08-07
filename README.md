# DBPA
Android Database Performance Analysis

This app makes some operations in mobile databases comparing performance at some NoSQL and the SQLite database.

This analysis considers the following databases:
1. [SQLite](https://www.sqlite.org/)
2. [Firebase](https://firebase.google.com/products/database/)
3. [Couchbase Lite](https://www.couchbase.com/products/mobile)
4. [iBoxDB](http://www.iboxdb.com/)
5. [SnappyDB](https://github.com/nhachicha/SnappyDB)
6. [Realm](https://github.com/realm/realm-java)

The operations are done by the `Analysis` class except by the Firebase class because it is event oriented and the `Analysis` class expects returns.

The number of iterations can be changed in the constant `ITERATIONS` in the class `Analysis` and will be reflected in all code.

The model used is very simple with only two classes: `Author` and `Post`. The relationship entity diagram can be seen in the image bellow:
![alt text](https://github.com/AmeliaPessoa/DBPA/blob/master/der.png "relationship entity diagram")

