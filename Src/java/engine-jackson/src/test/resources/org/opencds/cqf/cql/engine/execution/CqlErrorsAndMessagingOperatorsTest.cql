library ErrorsAndMessaging version '1.0'

define TestMessageInfo: Message(1, true, '100', 'Message', 'Test Message')
define TestMessageWarn: Message(2, true, '200', 'Warning', 'You have been warned!')
define TestMessageTrace: Message({3, 4, 5}, true, '300', 'Trace', 'This is a trace')
define TestMessageError: Message(3 + 1, true, '400', 'Error', 'This is an error!')
define TestMessageObfuscation: Message(Tuple { a: 1, b: Tuple { c: 2 } }, true, '400', 'Error', 'This source should be redacted')

define TestMessageWithNullSeverity:
  Message(1, true, '1', null as String, 'This is a message')

define TestMessageWithNullSource:
  Message(null, true, '1', 'Message', 'This is a message')

define TestMessageWithNullCondition:
  Message(1, null, '1', 'Message', 'This is a message')

define TestMessageWithNullCode:
  Message(1, true, null, 'Message', 'This is a message')

define TestMessageWithNullMessage:
  Message(1, true, '1', 'Message', null)

define TestWarningWithNullSource:
  Message(null, true, '1', 'Warning', 'This is a message')

define TestWarningWithNullCondition:
  Message(1, null, '1', 'Warning', 'This is a message')

define TestWarningWithNullCode:
  Message(1, true, null, 'Warning', 'This is a message')

define TestWarningWithNullMessage:
  Message(1, true, '1', 'Warning', null)

define TestTraceWithNullSource:
  Message(null, true, '1', 'Trace', 'This is a message')

define TestTraceWithNullCondition:
  Message(1, null, '1', 'Trace', 'This is a message')

define TestTraceWithNullCode:
  Message(1, true, null, 'Trace', 'This is a message')

define TestTraceWithNullMessage:
  Message(1, true, '1', 'Trace', null)

define TestErrorWithNullSource:
  Message(null, true, '1', 'Error', 'This is a message')

define TestErrorWithNullCondition:
  Message(1, null, '1', 'Error', 'This is a message')

define TestErrorWithNullCode:
  Message(1, true, null, 'Error', 'This is a message')

define TestErrorWithNullMessage:
  Message(1, true, '1', 'Error', null)