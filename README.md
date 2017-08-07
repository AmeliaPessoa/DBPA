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

Some adaptations must to be done in the model class for each database. For example, the iBox database doesn't allow embedded documents and for this reason, we have additional attributes to save only the keys for the embedded objects. For the same database, all attributes must be public.
For the Realm database, some annotations must be put in models class.

The analysis begins in the `MainActivity` class. It instantiates each class that represents each database.

For all databases, no one configuration tuning or indexes are created.

## Firebase Analysis
To run Firebase analysis is necessary to insert a personal key on the project. The instructions to make it is at this [link](https://firebase.google.com/docs/android/setup).

## License
This project is open source, contribution and feedback are welcomed.
```
MIT License

Copyright (c) 2017 Amélia Pessoa

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## How to contribute
An analysis in a new database can add with the following steps:
1. Insert new dependencies in the `gradle.config` file;
2. Create a new class inheriting from the `Analysis` class and implement the abstract methods;
3. Include in `MainActivity` class a call for the new class;
4. Modify the instructions in this `README` document if it is necessary.

Also to improve the current code, pull requests are welcomed.