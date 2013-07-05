# freqy #
freqy is a little app that generates a weighted list of the most frequent
words or pairs of words in a collection of documents.

The idea is that this set of keywords can be used to provide a quick overview
of a collection of documents without needing to look at each one and provide
a very rough and ready list of keywords (and pairs of words) for a collection
level description.

## Usage ##
Start the program with:

```
java -jar freqy-0.0.1-SNAPSHOT.jar
```

Pick a directory (the tool will automatically look into sub-directories)
for the collection.

Pick a file for the output summary file - this is html so use a .html extension.

Pick the number of words (n-grams where n can be 1, 2 or 3) to group.

Press "Go".

Make Tea.

Open the output file in your favourite browser.

## Warning! ##
Although built as a digital preservation/access tool and as such refuses to
write to any existing files, this software hasn't been extensively tested and will
be buggy.

You are advised to work on read-only (or second) copies of your data. 

The keyword analysis itself is very simple - only doing counts and
so may not be accurate (though when tested by archivists in Oxford, the values extracted
where felt to be reasonably accurate).

I've only tested this on data with hundreds of documents and text found is
written to a temporary file on the computer running freqy. Massive collections
will cause errors I suspect.

Sometimes Tika cannot parse a file and you'll see a warning on the console. These
files should be included in the final report BUT this is not working at present.

## Acknowledgements ##
freqy uses:

* cue.language : https://github.com/vcl/cue.language
* BEAM Word Cloud : https://github.com/petecliff/Cloud
* Apache Tika : http://tika.apache.org/

You can find the cue.language readme and licence under src/main/resources.

I created BEAM Word Cloud for the futureArch project at the Bodleian Library in 
Oxford so I'm permitting myself to use it.

## Build Instructions ##
You should be able to clone the project and then build it with:

```
mvn package
``` 

and you'll find a big jar file in target.

There is a dependency on the JavaFX runtime which is not (I think) in and repos.
To build the project I added the JavaFX runtime to my local repository:

```
mvn install:install-file -Dfile=<path to jfxrt.jar> -DgroupId=com.oracle -DartifactId=javafx-runtime -Dversion=2 =Dpackaging=jar
```

You might want to refine the version number.

Let me know if you have any problems/suggestions/improvements and have fun!

peter.cliff@bl.uk