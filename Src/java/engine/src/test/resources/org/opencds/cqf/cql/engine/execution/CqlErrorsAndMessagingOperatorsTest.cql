library CqlErrorsAndMessagingOperatorsTest

define TestMessageInfo: Message(1, true, '100', 'Message', 'Test Message')
define TestMessageWarn: Message(2, true, '200', 'Warning', 'You have been warned!')
define TestMessageTrace: Message({3, 4, 5}, true, '300', 'Trace', 'This is a trace')
define TestMessageError: Message(3 + 1, true, '400', 'Error', 'This is an error!')
define TestMessageObfuscation: Message(Tuple { a: 1, b: Tuple { c: 2 } }, true, '400', 'Error', 'This source should be redacted')

define TestErrorWithNullSource:
  Message(null, true, '1', 'Error', 'This is a message')

define TestErrorWithNullCode:
  Message(1, true, null, 'Error', 'This is a message')

define TestErrorWithNullMessage:
  Message(1, true, '1', 'Error', null)