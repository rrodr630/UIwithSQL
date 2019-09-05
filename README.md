# UIwithSQL
This project contains a functional UI that queries data from a database music.db, this file was provided by my Udemy instructor Tim Buchalka
Datasource is a singleton class that its only job is to query data from the database. You will see various string variables to build queries
that extract different datas.

I made use of advanced java features like:
* Databinding to bind the list that we can look at in the UI with any changes that the database 
suffers.
* Threads: a new thread is started to download the data from the UI so that the interface won't freeze until data is done downloading.
* Various methods from SQL like querrying data that is inner joined.
* Javafx: this is a whole world by itself. I haven't mastered it, but I can work my way around it.
