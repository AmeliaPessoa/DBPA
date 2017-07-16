# DBPA
Android Database Performance Analysis

This app make some operations in mobile databases comparing performance at some NoSQL and the SQLite database.

This analysis consider the following databases:
1. SQLite [link](https://www.sqlite.org/)
2. Firebase [link](https://firebase.google.com/products/database/)
3. Couchbase Lite [link](https://www.couchbase.com/products/mobile)
4. iBoxDB [link](http://www.iboxdb.com/)
5. SnappyDB [link](https://github.com/nhachicha/SnappyDB)

The operations are done by the `Analysis` class exceptby the Firebase class because it is event oriented and the `Analysis` class expect returns.

The number of iteractions can be changed in the constant `ITERACTIONS` in the class `Analysis` been reflected in the all code.

The model used is very simple with only two classes: `Author` and `Post`. The relationship entity diagram can be seen in the image bellow:
![alt text](https://github.com/AmeliaPessoa/DBPA/blob/master/der.png "relationship entity diagram")
