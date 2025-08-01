library TestUnion

define A: { { id: 1, name: 'Joe', phone: '123-4567' } }
define B: { { id: 1, name: 'Joe', phone: '123-4567' } }
define C: { { id: 1, name: 'Joe', phone: '123-4567' } }
define D: { { id: 1, name: 'Joe', phone: '123-4567' } }
define E: { { id: 1, name: 'Joe', phone: '123-4567' } }
define F: { { id: 1, name: 'Joe', phone: '123-4567' } }
define G: { { id: 1, name: 'Joe', phone: '123-4567' } }
define H: { { id: 1, name: 'Joe', phone: '123-4567' } }

define Union1: A union B
define Union2: A union B union C
define Union3: A union B union C union D
define Union4: A union B union C union D union E
define Union5: A union B union C union D union E union F
define Union6: A union B union C union D union E union F union G
define Union7: A union B union C union D union E union F union G union H
