### Trainer
---

_**Trainer**_ is a small scala + akka based application for generating fake data in csv format.

#### Usage
---

_**Trainer**_ is a command line utility & it accepts two input parameter. First being the input json file which defines the schema of the data
and second parameter is the path of the directory where it will write the generate csv files. E.g.

```
./trainer.sh --i <path>/input.json --o <outputpath>
```

#### Input Json
---

_**Trainer**_ expects an input json file which defines the schema of the data domain. For e.g. consider the following json input file:

```
{
  "tables": [
    {
      "name": "patients",
      "rowCount": 2000,
      "columns": [
        {
          "name": "patient_id",
          "handler": { "format": "Integer", "min": "0", "max": "100" }
        },
        {
          "name": "feed_name",
          "handler": { "format": "Constant", "value": "Test" }
        }
      ]
    },
    {
      "name": "encounters",
      "rowCount": 150000,
      "columns": [
        {
          "name": "encounter_id",
          "handler": { "format": "Integer", "minvalue": "100", "maxvalue": "500" }
        },
        {
          "name": "feed_name",
          "handler": { "format": "List", "listitems": "abc;epic;cerner;optum;", "seperator": ";" }
        },
        {
          "name": "patient_id",
          "handler": { "format": "Reference", "table": "patients", "column": "patient_id" }
        }
      ]
    }
  ]
}

```

Each node in the above json represents a table & within the node, we define the schema of the table i.e. columns & their data type. 

#### Handlers
---

_**Handlers** are independent piece of scala code capable of generating a given type of fake data. When defining columns, its mandatory to define the _**format**_
field & map it to one of the following predefined set of handlers. Apart from _**format**_, other metadata information might be required for data generation.

##### Boolean 

Randomly generates boolean values i.e. _true_ or _false_. E.g.
> "handler": { "format": "Boolean" }

##### Constant

Do not generate random value, instead use the provided value for all rows. E.g.
> "handler": { "format": "Constant", "value": "02" }

##### DateTime

Generates random date values in _yyyy-MM-dd_ format. Accepts min & max date values for limiting the date range. E.g.
> "handler": { "format": "DateTime", "minvalue": "2016-01-01", "maxvalue": "2016-10-01" }

##### Double

Generates random double values. Accepts _minvalue_ & _maxvalue_ parameters for limiting the range of generated values. E.g.
> "handler": { "format": "Double", "minvalue": "10", "maxvalue": "20" }

##### File

Reads values from file & uses it for data generation. File must contain data in single column but multiple rows manner.
> "handler": { "format": "File", "path": "/Users/foo/temp.txt" }

##### Guid

Generates random guid values.
> "handler": { "format": "Guid" }

##### Integer

Generates random integer values. Accepts _minvalue_ & _maxvalue_ parameters for limiting the range of generated values. E.g.
> "handler": { "format": "Integer", "minvalue": "10", "maxvalue": "20" }

##### List

Generates random value from provided list of values. 
"handler": { "format": "List", "listitems": "1;2;3;4;5;6", "seperator": ";" }

##### Reference

As the name suggests, generates random value based on another column data. Helpful in case of maintaining foreign key relationship.
> "handler": { "format": "Reference", "table": "encounters", "column": "encounter_id" }

##### Regex

Generates random data based on provided regex pattern.
> "handler": { "format": "Regex", "expression": "^\\d{5}$" }

##### Sequence 

Generates non-random integer data in increasing sequence.
> "handler": { "format": "Sequence", "increment": "1", "startvalue": "1" }

##### Text

Generates random strings containing alpha-numeric values.
> "handler": { "format": "Text", "minlength": "1", "minlength": "10" }

