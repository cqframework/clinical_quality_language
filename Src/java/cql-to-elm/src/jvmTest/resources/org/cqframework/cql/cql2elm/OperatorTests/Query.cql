library Query

define "Singular Source": Tuple { id: 1, name: 'John Doe' }
define "Plural Source": List { "Singular Source" }

define "Singular Source Query":
  "Singular Source" X where X.id = 1

define "Plural Source Query":
  "Plural Source" X where X.id = 1
  
define "Singular Multiple Source Query":
  from "Singular Source" S1, "Singular Source" S2
  where S1.id = S2.id

define "Plural Multiple Source Query":
  from "Plural Source" P1, "Plural Source" P2
  where P1.id = P2.id

define "Mixed Multiple Source Query":
  from "Singular Source" S, "Plural Source" P
  where S.id = P.id

define "Mixed Multiple Source Query 2":
  from "Plural Source" P, "Singular Source" S
  where S.id = P.id

define "Mixed Multiple Source Query With Return":
  from "Singular Source" S, "Plural Source" P
  where S.id = P.id
  return S

define "Singular Source With Plural Relationship":
  "Singular Source" S
    with "Plural Source" P such that S.id = P.id

define "Plural Source With Singular Relationship":
  "Plural Source" P
    with "Singular Source" S such that P.id = S.id

define "Singular Source With Plural Relationship And Return":
  "Singular Source" S
    with "Plural Source" P such that S.id = P.id
  return S

