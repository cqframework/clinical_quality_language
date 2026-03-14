library Issue223 version '1' 

define "List of Lists":
    {
        {
            Interval[1, 3],
            Interval[2, 5],
            Interval[3, 8]
        },
        {
            Interval[4, 6],
            Interval[5, 7],
            Interval[6, 9]
        },
        {
            Interval[7, 10],
            Interval[8, 11],
            Interval[9, 12]
        }
    }

define "Flatten List of Lists":
    flatten ( "List of Lists")

define "Access Flattened List of List Items":
    "Flatten List of Lists" Item
        where start of Item = 1
        return true

define "Access Flattened List of List Items in a Single Query":
    (flatten ( "List of Lists" )) FlattenedListofListsItem
         where start of FlattenedListofListsItem = 1
         return true
    
