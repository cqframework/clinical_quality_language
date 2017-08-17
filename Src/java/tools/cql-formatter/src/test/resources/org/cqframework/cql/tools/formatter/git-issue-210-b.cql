library Test version '0.0.000'

using QDM version '5.3'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Test":

		/* some coment here
			and another comment here
			and one more
		 */
	  exists (
			["Laboratory Test, Performed"]E
				where E.result > @2016-05-06
		)