library RxSig version '0.1.0'

context Unfiltered

/*************************************************************************/

define public function ParsedFreeTextSIG(SIG String) returns Any:
    SIG origSig
    let

        //----------------------------
        //----------------------------
        // get normalized sig
        //----------------------------
        //----------------------------

        normSig_Argument: origSig,

        // Purge characters that shouldn't appear in any raw SIG,
        // replace each with a space; we use a whitelist.
        normSig_FilterByCharWhitelist: ReplaceMatches(normSig_Argument, '[^ 0-9A-Z_a-z,;:!\\.\'"()@*/&#%+-]', ' '),

        // lower case all
        normSig_Lowercase: Lower(normSig_FilterByCharWhitelist),

        // ends in ', pain'
        indication_EarlyStageCheck: if EndsWith(normSig_Lowercase, ', pain') then 'pain' else null,
        normSig_ChopSfxPain: ReplaceMatches(normSig_Lowercase, ', pain$', ''),

        // remove commas, !, *, :, ;   replace with space
        normSig_DropMorePunct: ReplaceMatches(normSig_ChopSfxPain, '[,!*:;]', ' '),

        // remove please, the, of, with
        normSig_DropNoise: ReplaceMatchesAlphaWord(normSig_DropMorePunct, '(?:of|please|the|with)', ''),

        // convert all multi-hyphens to single hyphen
        normSig_MergeHyphens: ReplaceMatches(normSig_DropNoise, '-( *-)+', '-'),

        // convert "(s)" word suffix to "s", eg "tab(s)"->"tabs"
        normSig_Plurals: ReplaceMatches(normSig_MergeHyphens, '(?<=[a-z])\\(s\\)(?![a-z])', 's'),

        //---------------------------
        // numbers
        //---------------------------
        normSig_Numbers11: ReplaceMatchesAlphaWord(normSig_Plurals, 'two and( (a|one))? half', '2.5'),
        normSig_Numbers12: ReplaceMatchesDigitWord(normSig_Numbers11, '2 (1/2|0.5)', '2.5'),
        normSig_Numbers21: ReplaceMatchesAlphaWord(normSig_Numbers12, 'one and( (a|one))? half', '1.5'),
        normSig_Numbers22: ReplaceMatchesDigitWord(normSig_Numbers21, '1 (1/2|0.5)', '1.5'),
        normSig_Numbers31: ReplaceMatchesAlphaDigitWord(normSig_Numbers22, '(?:one|1)[ -]half', '0.5'),
        normSig_Numbers32: ReplaceMatchesAlphaWord(normSig_Numbers31, 'half', '0.5'),
        normSig_Numbers33: ReplaceMatchesDigitWord(normSig_Numbers32, '1/2', '0.5'),
        normSig_Numbers41: ReplaceMatchesAlphaDigitWord(normSig_Numbers33, '(?:one|1)[ -]fourth', '0.25'),
        normSig_Numbers51: ReplaceMatchesAlphaWord(normSig_Numbers41, 'one( full)?', '1'),
        normSig_Numbers52: ReplaceMatchesAlphaWord(normSig_Numbers51, 'two', '2'),
        normSig_Numbers53: ReplaceMatchesAlphaWord(normSig_Numbers52, 'three', '3'),
        normSig_Numbers54: ReplaceMatchesAlphaWord(normSig_Numbers53, 'four', '4'),
        normSig_Numbers55: ReplaceMatchesAlphaWord(normSig_Numbers54, 'five', '5'),
        normSig_Numbers56: ReplaceMatchesAlphaWord(normSig_Numbers55, 'six', '6'),
        normSig_Numbers57: ReplaceMatchesAlphaWord(normSig_Numbers56, 'seven', '7'),
        normSig_Numbers58: ReplaceMatchesAlphaWord(normSig_Numbers57, 'eight', '8'),
        normSig_Numbers59: ReplaceMatchesAlphaWord(normSig_Numbers58, 'nine', '9'),
        normSig_Numbers510: ReplaceMatchesAlphaWord(normSig_Numbers59, 'ten', '10'),
        normSig_Numbers511: ReplaceMatchesAlphaWord(normSig_Numbers510, 'eleven', '11'),
        normSig_Numbers512: ReplaceMatchesAlphaWord(normSig_Numbers511, 'twelve', '12'),
        normSig_Numbers61: ReplaceMatchesAlphaWord(normSig_Numbers512, '(\\d+) \\(\\1\\)', '$1'),

        normSig_Range: ReplaceMatches(normSig_Numbers61, '(\\d+\\.?\\d*) *(?:to|-|or) *(\\d+\\.?\\d*)','$1-$2'),

        normSig_Frequency1: ReplaceMatchesAlphaWord(normSig_Range, 'times', 'x'),
        normSig_Frequency2: ReplaceMatchesAlphaWord(normSig_Frequency1, 'once', '1x'),
        normSig_Frequency3: ReplaceMatchesAlphaWord(normSig_Frequency2, '(?:twice|bid)', '2x'),
        normSig_Frequency4: ReplaceMatchesAlphaWord(normSig_Frequency3, '(?:thrice|tid)', '3x'),
        normSig_Frequency5: ReplaceMatchesAlphaWord(normSig_Frequency4, 'qid', '4x'),
        normSig_Frequency6: ReplaceMatches(normSig_Frequency5, '(\\d+) *x', '$1x'),
        normSig_Frequency7: ReplaceMatches(normSig_Frequency6, '(\\d+)x?-(\\d+)x','$1-$2x'),

        // clean
        normSig_Clean1: CleanedStr(normSig_Frequency7),

        // extract out earliest fill dates
        fillDate_Argument: normSig_Clean1,
        fillDate_Pattern: '(?:earliest fill date|to be filled on(?: or after)?|(?:ok to|may) fill(?: on)?) +(?<month>\\d+)/(?<day>\\d+)/(?<year>\\d+)',
        fillDate_PatternWithRest: '.*?' + fillDate_Pattern + '.*',
        fillDate_IsMatch: Matches(fillDate_Argument, fillDate_PatternWithRest),
        fillDate_DateParts:
            if fillDate_IsMatch
            then Tuple {
                "year" : NormalizeYear(ToInteger(ReplaceMatches(fillDate_Argument, fillDate_PatternWithRest, '${year}'))),
                "month": ToInteger(ReplaceMatches(fillDate_Argument, fillDate_PatternWithRest, '${month}')),
                "day"  : ToInteger(ReplaceMatches(fillDate_Argument, fillDate_PatternWithRest, '${day}'))
            }
            else null,
        fillDate:
            if fillDate_IsMatch
            then DateTime(fillDate_DateParts."year", fillDate_DateParts."month", fillDate_DateParts."day")
            else null,
        normSig_FillDate: ReplaceMatches(fillDate_Argument, fillDate_Pattern, ''),

        normSig_Clean2: CleanedStr(normSig_FillDate),

        // extract out use dates
        useDates_Argument: normSig_Clean2,
        useDates_Pattern: '(?:(?:for )?use dates|for use|dates) +(?<startMonth>\\d+)/(?<startDay>\\d+)(?:/(?<startYear>\\d+))? *- *(?<endMonth>\\d+)/(?<endDay>\\d+)/(?<endYear>\\d+)',
        useDates_PatternWithRest: '.*?' + useDates_Pattern + '.*',
        useDates_IsMatch: Matches(useDates_Argument, useDates_PatternWithRest),
        useDates_EndDateParts:
            if useDates_IsMatch
            then Tuple {
                "year" : NormalizeYear(ToInteger(ReplaceMatches(useDates_Argument, useDates_PatternWithRest, '${endYear}'))),
                "month": ToInteger(ReplaceMatches(useDates_Argument, useDates_PatternWithRest, '${endMonth}')),
                "day"  : ToInteger(ReplaceMatches(useDates_Argument, useDates_PatternWithRest, '${endDay}'))
            }
            else null,
        useDates_MaybeStartYear: ReplaceMatches(useDates_Argument, useDates_PatternWithRest, '${startYear}'),
        useDates_StartDateParts:
            if useDates_IsMatch
            then Tuple {
                "year" :
                    if useDates_MaybeStartYear = ''
                    then useDates_EndDateParts."year"
                    else NormalizeYear(ToInteger(useDates_MaybeStartYear)),
                "month": ToInteger(ReplaceMatches(useDates_Argument, useDates_PatternWithRest, '${startMonth}')),
                "day"  : ToInteger(ReplaceMatches(useDates_Argument, useDates_PatternWithRest, '${startDay}'))
            }
            else null,
        endDate:
            if useDates_IsMatch
            then DateTime(useDates_EndDateParts."year", useDates_EndDateParts."month", useDates_EndDateParts."day")
            else null,
        useDates_StartDateNonAdjusted:
            if useDates_IsMatch
            then DateTime(useDates_StartDateParts."year", useDates_StartDateParts."month", useDates_StartDateParts."day")
            else null,
        startDate:
            if useDates_IsMatch
            then
                if useDates_StartDateNonAdjusted after endDate
                then useDates_StartDateNonAdjusted - 1 year // Note: Original Java logic did this even when MaybeStartYear nonempty
                else useDates_StartDateNonAdjusted
            else null,
        normSig_UseDates: ReplaceMatches(useDates_Argument, useDates_Pattern, ''),

        normSig_Clean3: CleanedStr(normSig_UseDates),

        //---------------------------
        // normalize different ways to say the same thing
        //---------------------------

        normSig_Take: ReplaceMatchesAlphaWord(normSig_Clean3, 'takes', 'take'),

        //---------------------------
        // shorthands
        //---------------------------

        // q
        normSig_Q: ReplaceMatchesAlphaWord(normSig_Take, 'eve(?:ry|r|y)', 'q'),

        //---------------------------
        // dose units
        //---------------------------

        // tab
        normSig_Tab: ReplaceMatchesAlphaWord(normSig_Q, '(?:tab(?:let)?s?|full tab)', 'tab'),

        // pill
        normSig_Pill: ReplaceMatchesAlphaWord(normSig_Tab, 'pills', 'pill'),

        // capsule
        normSig_Capsule: ReplaceMatchesAlphaWord(normSig_Pill, 'capsules', 'capsule'),

        // puff
        normSig_Puff: ReplaceMatchesAlphaWord(normSig_Capsule, 'puffs', 'puff'),

        // tsp
        normSig_Tsp: ReplaceMatchesAlphaWord(normSig_Puff, '(?:teaspoon(?:full?)?s?|tsps|tspfull?)', 'tsp'),

        // patch
        normSig_Patch: ReplaceMatchesAlphaWord(normSig_Tsp, 'patches', 'patch'),
        normSig_OnePatch: ReplaceMatchesAlphaWord(normSig_Patch, 'apply patch', 'apply 1 patch'), // special case

        // film
        normSig_Film: ReplaceMatchesAlphaWord(normSig_OnePatch, 'films', 'film'),

        // mg, mcg, ml, cc
        normSig_Measures: ReplaceMatchesAlphaWord(normSig_Film, '(mg|mcg|ml|cc)s', '$1'),

        //---------------------------
        // time units
        //---------------------------

        // hr
        normSig_Hr: ReplaceMatchesAlphaWord(normSig_Measures, 'h(?:ou)?rs?', 'hr'),

        // day
        normSig_Day: ReplaceMatchesAlphaWord(normSig_Hr, 'days', 'day'),

        // week
        normSig_Week: ReplaceMatchesAlphaWord(normSig_Day, 'w(?:ee)?ks?', 'week'),

        // month
        normSig_Month: ReplaceMatchesAlphaWord(normSig_Week, 'mo(?:nth)?s?', 'month'),

        //---------------------------
        // sig phrases
        //---------------------------

        // prn
        normSig_Prn1: ReplaceMatchesAlphaWord(normSig_Month, '(?:only )?(?:as|if) (?:need(?:ed)?|necessary)', 'prn'),
        normSig_Prn2: ReplaceMatchesAlphaWord(normSig_Prn1, 'prn prn', 'prn'),

        //---------------------------
        // routes
        //---------------------------

        // PO
        normSig_PO: ReplaceMatchesAlphaWord(normSig_Prn2, '(?:by mouths?|orally|po po)', 'po'),

        // sublinqual
        normSig_SL: ReplaceMatchesAlphaWord(normSig_PO, '(?:sublingual|under tongue)', 'sl'),

        // feeding tube
        normSig_Tube: ReplaceMatchesAlphaWord(normSig_SL, 'per feeding tube', 'feeding tube'),

        // skin
        normSig_Skin1: ReplaceMatchesAlphaWord(normSig_Tube, '(?:externally )?to skin', 'skin'),
        normSig_Skin2: ReplaceMatchesAlphaWord(normSig_Skin1, '(?:and change|to be applied)', ''),

        // every day
        normSig_EveryDay: ReplaceMatchesAlphaWord(normSig_Skin2, '(?:daily|(?:a|per) day|q ?day|qd)', 'every day'),

        // every afternoon
        normSig_EveryAft: ReplaceMatchesAlphaWord(normSig_EveryDay, 'in afternoon', 'every afternoon'),

        // every night/evening/bedtime
        normSig_EveryNight1: ReplaceMatchesAlphaWord(normSig_EveryAft, '(?:q ?|in )(evening|hs|night|pm)', 'every night'),
        normSig_EveryNight2: ReplaceMatchesAlphaWord(normSig_EveryNight1, '(?:hs|(?<!\\d)pm|nightly)', 'every night'),
        normSig_EveryNight3: ReplaceMatchesAlphaWord(normSig_EveryNight2, '(?:at|before|every) (bed|night)( ?time)?', 'every night'),
        normSig_EveryNight4: ReplaceMatchesAlphaWord(normSig_EveryNight3, 'every (?:night|day) every night', 'every night'),

        // every morning
        normSig_EveryMorn1: ReplaceMatchesAlphaWord(normSig_EveryNight4, '(?:(?:q ?|in )(?:morning|am)|(?<!\\d)am)', 'every morning'),
        normSig_EveryMorn2: ReplaceMatchesAlphaWord(normSig_EveryMorn1, 'every (?:day every morning|morning every day)', 'every morning'),

        // combos
        normSig_Combo3: ReplaceMatchesAlphaWord(normSig_EveryMorn2, 'every morning and every night', '2x'),

        normSig_Clean4: CleanedStr(normSig_Combo3),

        // remove a, an
        normSig_A: ReplaceMatchesAlphaWord(normSig_Clean4, 'a', ''),
        normSig_An: ReplaceMatchesAlphaWord(normSig_A, 'an', ''),

        // excess hyphen in some sigs
        normSig_ExcessHyphen: ReplaceMatches(normSig_An, '(\\d+ ?)-( ?\\D)', '$1$2'),
        normSig_MergeSpaces: ReplaceMatches(normSig_ExcessHyphen, ' {2,}', ' '),

        // Trim bounding whitespace.
        normSig_TrimSpaces1: ReplaceMatches(normSig_MergeSpaces, '^ *(.*?) *$', '$1'),
        normSig_TrimSpaces2: ReplaceMatches(normSig_TrimSpaces1, '\\( *(.*?) *\\)', '($1)'),

        normSig: normSig_TrimSpaces2,

        //----------------------------
        //----------------------------
        // extract out known elements
        //----------------------------
        //----------------------------

        leftoverSig_Argument: normSig,

        // ignorable phrases
        ignorSig_Argument: Tuple { ignorable: null as String, leftover: leftoverSig_Argument },
        ignorSig_11: ProcessIgnorableWordPhrase(ignorSig_Argument, 'do not combine other tylenol products'),
        ignorSig_12: ProcessIgnorableWordPhrase(ignorSig_11, 'can take stool softener if constipation occurs'),
        ignorSig_13: ProcessIgnorableWordPhrase(ignorSig_12, 'may cause drowsiness'),
        ignorSig_14: ProcessIgnorableWordPhrase(ignorSig_13, '(?:as it )?(?:may sedate|is sedating)'),
        ignorSig_15: ProcessIgnorableWordPhrase(ignorSig_14, 'may fill early for travel'),
        ignorSig_21: ProcessIgnorableWordPhrase(ignorSig_15, 'do not drive on medication'),
        ignorSig_22: ProcessIgnorableWordPhrase(ignorSig_21, 'must be seen for further refills'),
        ignorSig_23: ProcessIgnorableWordPhrase(ignorSig_22, 'may (?:re)?fil.+?(?:(?:before|prior to) use dates?|day prior|fill date|same day|today)'),
        ignorSig_24: ProcessIgnorableWordPhrase(ignorSig_23, 'not valid without seal'),
        ignorSig_25: ProcessIgnorableWordPhrase(ignorSig_24, 'bring to appointment'),
        ignorSig_31: ProcessIgnorableWordPhrase(ignorSig_25, 'needs.+?brand'),
        ignorSig_32: ProcessIgnorableWordPhrase(ignorSig_31, 'this is(?: a)?'),
        ignorSig_33: ProcessIgnorableWordPhrase(ignorSig_32, 'no early refills'),
        ignorSig_34: ProcessIgnorableWordPhrase(ignorSig_33, 'do not fill early'),
        ignorSig_35: ProcessIgnorableWordPhrase(ignorSig_34, 'attending to sign'),
        ignorSig_41: ProcessIgnorableWordPhrase(ignorSig_35, 'no further refills from pmc'),
        ignorSig_42: ProcessIgnorableWordPhrase(ignorSig_41, '(?:use )?as directed'),
        ignorSig_43: ProcessIgnorableWordPhrase(ignorSig_42, 'wean asap'),
        ignorSig_44: ProcessIgnorableWordPhrase(ignorSig_43, '(?:and )?change'),
        ignorSig_51: ProcessIgnorableWordPhrase(ignorSig_44, 'avoid with driving or work'),
        ignorSig_52: ProcessIgnorableWordPhrase(ignorSig_51, 'try to minimize overall'),
        ignorSig_53: ProcessIgnorableWordPhrase(ignorSig_52, 'but'),
        ignorSig_54: ProcessIgnorableWordPhrase(ignorSig_53, 'self pay(?: portion)?'),
        ignorSig_55: ProcessIgnorableWordPhrase(ignorSig_54, 'insurance portion'),
        ignorSig_61: ProcessIgnorableWordPhrase(ignorSig_55, 'instructions in spanish'),
        ignorSig_62: ProcessIgnorableWordPhrase(ignorSig_61, 'do not combine'),
        ignorSig_63: ProcessIgnorableWordPhrase(ignorSig_62, 'day time'),
        ignorSig_64: ProcessIgnorableWordPhrase(ignorSig_63, 'daw'),
        ignorSig_65: ProcessIgnorableWordPhrase(ignorSig_64, 'alternate.+?(?:oxy|hydro)codone'),
        ignorSig_71: ProcessIgnorableWordPhrase(ignorSig_65, 'brand.+?(?:necessary|only)'),
        ignorSig_72: ProcessIgnorableWordPhrase(ignorSig_71, 'do not.+?(?:sedate|on med(?:ication)?)'),
        ignorSig_73: ProcessIgnorableWordPhrase(ignorSig_72, 'prior.+?skin care'),
        ignorSig_74: ProcessIgnorableWordPhrase(ignorSig_73, '(?:do not exceed|max).+?acetaminophen.+?(?:24 ?hr|day)'),
        ignorSig_75: ProcessIgnorableWordPhrase(ignorSig_74, 'dr.+?to sign only'),
        ignorSig_81: ProcessIgnorableWordPhrase(ignorSig_75, 'please notify.+?ready'),
        ignorSig_82: ProcessIgnorableWordPhrase(ignorSig_81, '(?:hydro|r?oxy)codone'),
        ignorSig: ignorSig_82.ignorable,
        leftoverSig_SansIgnorables: ignorSig_82.leftover,

        // commands: take, apply (starts with)
        command_Argument: leftoverSig_SansIgnorables,
        command_Pattern: '^((?:to )?(?:apply|dissolve|place|take)|may take|take up to)(?![a-z])',
        command:
            if Matches(command_Argument, command_Pattern + '.*')
            then ReplaceMatches(command_Argument, '(' + command_Pattern + ').*', '$1')
            else null,
        leftoverSig_Command: ReplaceMatches(command_Argument, command_Pattern, ''),

        // prn
        isPrn_Argument: Tuple { isPrn: false, indication: indication_EarlyStageCheck, leftover: leftoverSig_Command },
        isPrn_1: ProcessWordPrnReasonWithLabel(isPrn_Argument, '.*?pain(?: control)?', 'pain'),
        isPrn_2: ProcessWordPrnReason(isPrn_1, 'cough and congestion'),
        isPrn_3: ProcessWordPrnReason(isPrn_2, 'headaches?'),
        isPrn_4: ProcessWordPrnReason(isPrn_3, 'nausea and stomach cramps'),
        isPrn_5: ProcessWordPrnReasonWithLabel(isPrn_4, '.*?cough', 'cough'),
        isPrn_6: ProcessWordPrnReason(isPrn_5, '(?:restless leg(?: syndrome|s)|rls)'),
        isPrn_7: ProcessWordPrnReason(isPrn_6, 'opioid dependence'),
        isPrn_MainChecks: isPrn_7.isPrn,

        // indication not part of prn
        indication_Argument: Tuple { indication: isPrn_7.indication, leftover: isPrn_7.leftover },
        indication_1: ProcessWordIndicationWithLabel(indication_Argument, '.*?pain(?: control)?', 'pain'),
        indication_2: ProcessWordIndication(indication_1, 'cough(?: and congestion)?'),
        indication_3: ProcessWordIndication(indication_2, 'headaches?'),
        indication_4: ProcessWordIndication(indication_3, 'nausea and stomach cramps'),
        indication_5: ProcessWordIndication(indication_4, '(?:restless leg(?: syndrome|s)|rls)'),
        indication_6: ProcessWordIndication(indication_5, 'opioid dependence'),
        indication: indication_6.indication,

        // duration
        duration_Argument: Tuple {
            "duration": null as Integer,
            durationTimeUnits: null as String,
            leftover: indication_6.leftover,
            durationAlreadyParsed: false
        },
        duration_11: ProcessWordDuration(duration_Argument, 'for', 'day(?: (?:supply|rx))?', 'd'),
        duration_12: ProcessWordDuration(duration_11, 'for', 'week(?: (?:supply|rx))?', 'wk'),
        duration_13: ProcessWordDuration(duration_12, 'for', 'month(?: (?:supply|rx))?', 'mo'),
        duration_21: ProcessWordDuration(duration_13, 'each rx should last for', 'day', 'd'),
        duration_22: ProcessWordDuration(duration_21, 'each rx should last for', 'week', 'wk'),
        duration_23: ProcessWordDuration(duration_22, 'each rx should last for', 'month', 'mo'),
        duration_31: ProcessWordDuration(duration_23, 'must last at least(?: to last)?', 'day', 'd'),
        duration_32: ProcessWordDuration(duration_31, 'must last at least(?: to last)?', 'week', 'wk'),
        duration_33: ProcessWordDuration(duration_32, 'must last at least(?: to last)?', 'month', 'mo'),
        duration_41: ProcessWordDuration(duration_33, 'each fill must last', 'day', 'd'),
        duration_42: ProcessWordDuration(duration_41, 'each fill must last', 'week', 'wk'),
        duration_43: ProcessWordDuration(duration_42, 'each fill must last', 'month', 'mo'),
        duration_51: ProcessWordDuration(duration_43, 'must last?', 'day', 'd'),
        duration_52: ProcessWordDuration(duration_51, 'must last?', 'week', 'wk'),
        duration_53: ProcessWordDuration(duration_52, 'must last?', 'month', 'mo'),
        duration_61: ProcessWordDuration(duration_53, 'to last(?: at least)?', 'day', 'd'),
        duration_62: ProcessWordDuration(duration_61, 'to last(?: at least)?', 'week', 'wk'),
        duration_63: ProcessWordDuration(duration_62, 'to last(?: at least)?', 'month', 'mo'),
        duration_71: ProcessWordDuration(duration_63, '', 'day (?:supply|rx)', 'd'),
        duration_72: ProcessWordDuration(duration_71, '', 'week (?:supply|rx)', 'wk'),
        duration_73: ProcessWordDuration(duration_72, '', 'month (?:supply|rx)', 'mo'),
        duration_81: ProcessWordDuration(duration_73, 'x', 'day', 'd'),
        duration_82: ProcessWordDuration(duration_81, 'x', 'week', 'wk'),
        duration_83: ProcessWordDuration(duration_82, 'x', 'month', 'mo'),
        "duration": duration_83."duration",
        durationTimeUnits: duration_83.durationTimeUnits,

        // daily max
        dailyMax_Argument: Tuple {
            maxDailyFrequency: null as Integer,
            maxDailyDose: null as Decimal,
            maxDailyDoseUnits: null as String,
            leftover: duration_83.leftover
        },
        dailyMax_commonSuffixRegex: '(?:(?:in )?every day|/?day|(?:in )?24 ?hr|per 24hr day)',
        dailyMax_1: ProcessWordDailyMax(dailyMax_Argument, '(?:(?:for|up ?to) )?(?:max(?:imum)?|ave(?:rage)?|avg)', dailyMax_commonSuffixRegex),
        dailyMax_2: ProcessWordDailyMax(dailyMax_1, '(?:do not|not to|to) exceed', dailyMax_commonSuffixRegex),
        dailyMax_3: ProcessWordDailyMax(dailyMax_2, 'total', dailyMax_commonSuffixRegex),
        dailyMax_4: ProcessWordDailyMax(dailyMax_3, '(?:(?:limit|for) )?up ?to', dailyMax_commonSuffixRegex),
        dailyMax_5: ProcessWordDailyMax(dailyMax_4, 'for', '(?:every day|/day|(?:in )?24 ?hr|per 24hr day)'),
        dailyMax_6: ProcessWordDailyMax(dailyMax_5, '\\(', 'every day\\)'),
        dailyMax_7: ProcessWordDailyMax(dailyMax_6, 'or', 'every day'),
        maxDailyFrequency: dailyMax_7.maxDailyFrequency,
        maxDailyDose: dailyMax_7.maxDailyDose,
        maxDailyDoseUnits: dailyMax_7.maxDailyDoseUnits,

        // process route
        route_Argument: CleanedStr(dailyMax_7.leftover),
        // TODO: What to choose when for example "po or sl" is in the SIG?
        route:
            if Matches(route_Argument, '.*?(?<![a-z])(?:po|sl|skin|feeding tube)(?![a-z]).*')
            then ReplaceMatches(route_Argument, '.*?(?<![a-z])(po|sl|skin|feeding tube)(?![a-z]).*', '$1')
            else null,
        leftoverSig_Route: ReplaceMatchesAlphaWord(route_Argument, '(?:po|sl|skin|feeding tube)', ''),

        // process dosing
        dosing_Argument: CleanedStr(leftoverSig_Route),
        // assume dose starts at beginning at this point
        // TODO: What to do when dose is expressed as a sum of multiple units, eg "1 30mg tab and 1 60mg tab"?
        dosing_Pattern: '^'
            + '(?<doseLow>\\d+\\.?\\d*)(?:-(?<doseHigh>\\d+\\.?\\d*))? ?(?<doseUnits>' + DoseUnitsOpts + ')?'
            + ' ?(?:\\((?<altDoseSig>'
            + '.*' // should be '.*?' instead?
            + ')\\))?',
        dosing_PatternWithRest: dosing_Pattern + '.*',
        dosing_IsMatch: Matches(dosing_Argument, dosing_PatternWithRest),
        doseLow: if dosing_IsMatch then ToDecimal(ReplaceMatches(dosing_Argument, dosing_PatternWithRest, '${doseLow}')) else null,
        doseHighStr: if dosing_IsMatch then ReplaceMatches(dosing_Argument, dosing_PatternWithRest, '${doseHigh}') else '',
        doseHigh: if doseHighStr = '' then doseLow else ToDecimal(doseHighStr),
        doseUnits: if dosing_IsMatch then NullIfEmptyString(ReplaceMatches(dosing_Argument, dosing_PatternWithRest, '${doseUnits}')) else null,
        altDoseSig: if dosing_IsMatch then ReplaceMatches(dosing_Argument, dosing_PatternWithRest, '${altDoseSig}') else '',
        altDosing_Pattern: '(?<altDoseLow>\\d+\\.?\\d*)(?:-(?<altDoseHigh>\\d+\\.?\\d*))? ?(?<altDoseUnits>' + DoseUnitsOpts + ')?',
        altDosing_PatternWithRest: altDosing_Pattern + '.*',
        altDosing_IsMatch: Matches(altDoseSig, altDosing_PatternWithRest),
        altDoseLow: if altDosing_IsMatch then ToDecimal(ReplaceMatches(altDoseSig, altDosing_PatternWithRest, '${altDoseLow}')) else null,
        altDoseHighStr: if altDosing_IsMatch then ReplaceMatches(altDoseSig, altDosing_PatternWithRest, '${altDoseHigh}') else '',
        altDoseHigh: if altDoseHighStr = '' then altDoseLow else ToDecimal(altDoseHighStr),
        altDoseUnits: if altDosing_IsMatch then NullIfEmptyString(ReplaceMatches(altDoseSig, altDosing_PatternWithRest, '${altDoseUnits}')) else null,
        leftoverSig_Dosing: ReplaceMatches(dosing_Argument, dosing_Pattern, ''),

        // process interval
        interval_Argument: CleanedStr(leftoverSig_Dosing),
        // q_X-Y_h(r) pattern
        interval_Pattern: '\\bq ?(?<intervalLow>\\d+)(?:-(?<intervalHigh>\\d+))? ?(?<intervalTimeUnits>hr?|month|week|day|prn)?\\b',
        interval_PatternWithRest: '.*?' + interval_Pattern + '.*',
        interval_IsMatch: Matches(interval_Argument, interval_PatternWithRest),
        intervalLow: if interval_IsMatch then ToInteger(ReplaceMatches(interval_Argument, interval_PatternWithRest, '${intervalLow}')) else null,
        intervalHighStr: if interval_IsMatch then ReplaceMatches(interval_Argument, interval_PatternWithRest, '${intervalHigh}') else '',
        intervalHigh: if intervalHighStr = '' then intervalLow else ToInteger(intervalHighStr),
        intervalTimeUnitsStr: if interval_IsMatch then ReplaceMatches(interval_Argument, interval_PatternWithRest, '${intervalTimeUnits}') else '',
        intervalTimeUnits:
            if interval_IsMatch
            then
                if intervalTimeUnitsStr in List {'', 'h', 'hr', 'prn'}
                then 'h'
                else if intervalTimeUnitsStr = 'day'
                then 'd'
                else intervalTimeUnitsStr
            else null,
        isPrn_Interval: isPrn_MainChecks or intervalTimeUnitsStr = 'prn',
        leftoverSig_Interval: ReplaceMatches(interval_Argument, interval_Pattern, ''),

        // process frequency
        frequency_Argument: CleanedStr(leftoverSig_Interval),
        frequency_Pattern: '^(?:prn)? ?(?<frequencyRange>(?<frequencyLow>\\d+)x?(?:-(?<frequencyHigh>\\d+))?x)? ?(?:(?<frequencyTimeUnits>every (?:day|night|morning)|prn)|$)',
        frequency_PatternWithRest: frequency_Pattern + '.*',
        frequency_IsMatch: Matches(frequency_Argument, frequency_PatternWithRest),
        frequency_Range: if frequency_IsMatch then ReplaceMatches(frequency_Argument, frequency_PatternWithRest, '${frequencyRange}') else '',
        frequencyLowStr: if frequency_IsMatch then ReplaceMatches(frequency_Argument, frequency_PatternWithRest, '${frequencyLow}') else '',
        frequencyHighStr: if frequency_IsMatch then ReplaceMatches(frequency_Argument, frequency_PatternWithRest, '${frequencyHigh}') else '',
        frequencyTimeUnitsStr: if frequency_IsMatch then ReplaceMatches(frequency_Argument, frequency_PatternWithRest, '${frequencyTimeUnits}') else '',
        frequency_HaveResult: frequency_IsMatch and (frequency_Range != '' or StartsWith(frequencyTimeUnitsStr, 'every')),
        frequencyLow: if frequency_HaveResult then if frequencyLowStr = '' then 1 else ToInteger(frequencyLowStr) else null,
        frequencyHigh: if frequency_HaveResult then if frequencyHighStr = '' then frequencyLow else ToInteger(frequencyHighStr) else null,
        frequencyTimeUnits: if frequency_HaveResult then 'd' else null,
        isPrn_Frequency: isPrn_Interval or (frequency_HaveResult and frequencyTimeUnitsStr = 'prn'),
        leftoverSig_Frequency: if frequency_HaveResult then ReplaceMatches(frequency_Argument, frequency_Pattern, '') else frequency_Argument,

        // cleanup
        cleanup_Argument: CleanedStr(leftoverSig_Frequency),
        isPrn_Cleanup: isPrn_Frequency or Matches(cleanup_Argument, '.*? *(?<![a-z])prn'),
        leftoverSig_Cleanup: ReplaceMatches(cleanup_Argument, ' *(?<![a-z])prn$', ''),
        isPrn: isPrn_Cleanup or leftoverSig_Cleanup = 'prn (',
        leftoverSig: if Matches(leftoverSig_Cleanup, '(?:prn \\(|\\(|\\))') then '' else leftoverSig_Cleanup

    return Tuple {
         alternateDoseHigh: altDoseHigh,
         alternateDoseLow: altDoseLow,
         alternateDoseUnits: altDoseUnits,
         command: command,
         doseHigh: doseHigh,
         doseLow: doseLow,
         doseUnits: doseUnits,
         "duration": "duration",
         durationTimeUnits: durationTimeUnits,
         earliestFillDate: fillDate,
         endDate: endDate,
         frequencyHigh: frequencyHigh,
         frequencyLow: frequencyLow,
         frequencyTimeUnits: frequencyTimeUnits,
         ignorableSig: ignorSig,
         indication: indication,
         intervalHigh: intervalHigh,
         intervalLow: intervalLow,
         intervalTimeUnits: intervalTimeUnits,
         isPrn: isPrn,
         leftoverSig: leftoverSig,
         maxDailyDose: maxDailyDose,
         maxDailyDoseUnits: maxDailyDoseUnits,
         maxDailyFrequency: maxDailyFrequency,
         normalizedSig: normSig,
         originalSig: origSig,
         route: route,
         startDate: startDate
    }

/*************************************************************************/

define private function ProcessIgnorableWordPhrase(
        states List<Tuple {ignorable String, leftover String}>, phrase String)
        returns Tuple {ignorable String, leftover String}:
    SingletonFrom(
    states S
    return
        if Matches(S.leftover, '.*?\\b' + phrase + '\\b.*')
        then Tuple {
            ignorable: (if S.ignorable is null then '' else S.ignorable + '; ')
                + ReplaceMatches(S.leftover, '.*?\\b(' + phrase + ')\\b.*', '$1'),
            leftover: ReplaceMatchesAlphaDigitWord(S.leftover, phrase, '')
        }
        else S
    )

/*************************************************************************/

define private function ProcessWordPrnReason(
        states List<Tuple {isPrn Boolean, indication String, leftover String}>, reasonRegex String)
        returns Tuple {isPrn Boolean, indication String, leftover String}:
    ProcessWordPrnReasonWithLabel(states, reasonRegex, null as String)

define private function ProcessWordPrnReasonWithLabel(
        states List<Tuple {isPrn Boolean, indication String, leftover String}>,
        reasonRegex String, reasonLabel List<String>)
        returns Tuple {isPrn Boolean, indication String, leftover String}:
    SingletonFrom(
    states S
    return
        if Matches(S.leftover, '.*?\\bprn(?: for)? ' + reasonRegex + '\\b.*')
        then Tuple {
            isPrn: true,
            indication:
                if SingletonFrom(reasonLabel) is null
                then ReplaceMatches(S.leftover, '.*?\\bprn(?: for)? (' + reasonRegex + ')\\b.*', '$1')
                else SingletonFrom(reasonLabel),
            leftover: ReplaceMatchesAlphaDigitWord(S.leftover, 'prn(?: for)? ' + reasonRegex, '')
        }
        else S
    )

define private function ProcessWordIndication(
        states List<Tuple {indication String, leftover String}>, indicRegex String)
        returns Tuple {indication String, leftover String}:
    ProcessWordIndicationWithLabel(states, indicRegex, null as String)

define private function ProcessWordIndicationWithLabel(
        states List<Tuple {indication String, leftover String}>,
        indicRegex String, indicLabel List<String>)
        returns Tuple {indication String, leftover String}:
    SingletonFrom(
    states S
    return
        if Matches(S.leftover, '.*?\\b(?:for|indications?) ' + indicRegex + '\\b.*')
        then Tuple {
            indication:
                if SingletonFrom(indicLabel) is null
                then ReplaceMatches(S.leftover, '.*?\\b(?:for|indications?) (' + indicRegex + ')\\b.*', '$1')
                else SingletonFrom(indicLabel),
            leftover: ReplaceMatchesAlphaDigitWord(S.leftover, '(?:for|indications?) ' + indicRegex, '')
        }
        else S
    )

/*************************************************************************/

define private function ProcessWordDuration(
        states List<Tuple {"duration" Integer, durationTimeUnits String, leftover String, durationAlreadyParsed Boolean}>,
        prefixRegex String, suffixRegex String, unitsLabel String)
        returns Tuple {"duration" Integer, durationTimeUnits String, leftover String, durationAlreadyParsed Boolean}:
    SingletonFrom(
    states S
    return
        if Matches(S.leftover, '.*?\\b' + prefixRegex + ' *\\d+ *' + suffixRegex + '\\b.*')
        then
            if S.durationAlreadyParsed
                or Matches(S.leftover, '.*?\\b' + prefixRegex + ' *\\d+ *' + suffixRegex
                    + '\\b.*?\\b' + prefixRegex + ' *\\d+ *' + suffixRegex + '\\b.*')
            // avoid processing cases where there are multiple patterns, e.g., take 1 tab for x days, 1/5 tab for y days
            then Tuple {
                "duration": null as Integer,
                durationTimeUnits: null as String,
                leftover: ReplaceMatchesAlphaDigitWord(S.leftover, prefixRegex + ' *\\d+ *' + suffixRegex, ''),
                durationAlreadyParsed: true
            }
            else Tuple {
                "duration": ToInteger(ReplaceMatches(S.leftover, '.*?\\b' + prefixRegex + ' *(\\d+) *' + suffixRegex + '\\b.*', '$1')),
                durationTimeUnits: unitsLabel,
                leftover: ReplaceMatchesAlphaDigitWord(S.leftover, prefixRegex + ' *\\d+ *' + suffixRegex, ''),
                durationAlreadyParsed: true
            }
        else S
    )

/*************************************************************************/

define private function ProcessWordDailyMax(
        states List<Tuple {maxDailyFrequency Integer, maxDailyDose Decimal, maxDailyDoseUnits String, leftover String}>,
        prefixRegex String, suffixRegex String)
        returns Tuple {maxDailyFrequency Integer, maxDailyDose Decimal, maxDailyDoseUnits String, leftover String}:
    SingletonFrom(
    states S
    return
        if Matches(S.leftover, '.*?(?<!\w)' + prefixRegex + ' ?\\d+ ?x ?' + suffixRegex + '(?!\w).*')
        then Tuple {
            maxDailyFrequency: ToInteger(ReplaceMatches(S.leftover, '.*?(?<!\w)' + prefixRegex + ' ?(\\d+) ?x ?' + suffixRegex + '(?!\w).*', '$1')),
            maxDailyDose: S.maxDailyDose,
            maxDailyDoseUnits: S.maxDailyDoseUnits,
            leftover: ReplaceMatchesAlphaDigitWord(S.leftover, prefixRegex + ' ?\\d+ ?x ?' + suffixRegex, '')
        }
        else if Matches(S.leftover, '.*?(?<!\w)' + prefixRegex + ' ?\\d+\\.?\\d* ?(?:' + DoseUnitsOpts + ')? ?' + suffixRegex + '(?!\w).*')
        then Tuple {
            maxDailyFrequency: S.maxDailyFrequency,
            maxDailyDose: ToDecimal(ReplaceMatches(S.leftover, '.*?(?<!\w)' + prefixRegex + ' ?(\\d+\\.?\\d*) ?(?:' + DoseUnitsOpts + ')? ?' + suffixRegex + '(?!\w).*', '$1')),
            maxDailyDoseUnits: NullIfEmptyString(ReplaceMatches(S.leftover, '.*?(?<!\w)' + prefixRegex + ' ?\\d+\\.?\\d* ?(' + DoseUnitsOpts + ')? ?' + suffixRegex + '(?!\w).*', '$1')),
            leftover: ReplaceMatches(S.leftover, '(?<!\w)' + prefixRegex + ' ?\\d+\\.?\\d* ?(?:' + DoseUnitsOpts + ')? ?' + suffixRegex + '(?!\w)', '')
        }
        else S
    )

/*************************************************************************/

/**
* Intended to be called iteratively after other processing.  Contains methods for cleaning issues with strings that may arise repetitively.
* @param strUnchangedByFunction
* @return
*/
define private function CleanedStr(value String) returns String:
    value str
    let

        // convert all multi-space spaces to single space
        mergeSpaces: ReplaceMatches(str, ' {2,}', ' '),

        // remove all leading periods and spaces
        dropPfxPeriods: ReplaceMatches(mergeSpaces, '^[\\. ]*', ''),

        // remove periods following a non-number character
        dropNonNumPeriods: ReplaceMatches(dropPfxPeriods, '(?<=\\D)\\.', ' '),

        // remove hyphens following a non-number character (unless it's a space or d, e.g., bid-tid)
        // but first add a space if hyphen precedes an alpha char, so we don't end up catenating words foo-bar into foobar
        dropNonNumHyphens1: ReplaceMatches(dropNonNumPeriods, '(?<=[^\\d d])-(?=[a-z])', '- '),
        dropNonNumHyphens2: ReplaceMatches(dropNonNumHyphens1, '(?<=[^\\d d])-', ''),

        // remove ending periods and hyphens and spaces
        dropSfxPeriodsHyphens: ReplaceMatches(dropNonNumHyphens2, '[\\. -]*$', ''),

        // remove paired parenthesis having no contents
        dropEmptyParens: ReplaceMatches(dropSfxPeriodsHyphens, '\\( *\\)', '')

    return dropEmptyParens

/*************************************************************************/

/* Replace whole words as defined by not having any adjacent letters; assume all arguments are lowercase. */
/* Searching for [a-z] is more specific than \b which also allows digits. */
define private function ReplaceMatchesAlphaWord(phrase String, oldWordPattern String, newWordTemplate String) returns String:
    ReplaceMatches(phrase, '(?<![a-z])' + oldWordPattern + '(?![a-z])', newWordTemplate)

define private function ReplaceMatchesDigitWord(phrase String, oldWordPattern String, newWordTemplate String) returns String:
    ReplaceMatches(phrase, '(?<!\\d)' + oldWordPattern + '(?!\\d)', newWordTemplate)

define private function ReplaceMatchesAlphaDigitWord(phrase String, oldWordPattern String, newWordTemplate String) returns String:
    ReplaceMatches(phrase, '\\b' + oldWordPattern + '\\b', newWordTemplate)

define private function NullIfEmptyString(value String) returns String:
    if value ~ '' then null else value

/*************************************************************************/

// The original Java version of this library used SimpleDateFormat.parse('MM/dd/yy')
// and so we resemble its behavior by treating a year less than 100 as relative;
// a year in 0-39 is treated as 20xx while 40-99 is treated as 19xx.
// Technically the original did +20/-80 on the today or some such but we will be deterministic instead.
// As of this writing it is early 2018 so the following is within a year of the today logic for now.
define private function NormalizeYear(y Integer) returns Integer:
         if y >= 100 then y
    else if y >=  40 then 1900+y
    else if y >=   0 then 2000+y
    else                  y

define private DoseUnitsOpts: 'tab|pill|capsule|patch|film|mg|mcg|ml|cc|tsp'

/*************************************************************************/
