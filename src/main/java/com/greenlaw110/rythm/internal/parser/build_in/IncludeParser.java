/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class IncludeParser extends KeywordParserFactory {

    private static final String R = "(^%s(%s\\s*((?@()))\\s*))";

    public IncludeParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new RemoveLeadingLineBreakAndSpacesParser(c) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @include statement. Correct usage: @include(\"foo.bar, a.b.c, ...\")");
                }
                int lineNo = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(3);
                if (S.isEmpty(s)) {
                    raiseParseException("Error parsing @include statement. Correct usage: @include(\"foo.bar, a.b.c, ...\")");
                }
                s = S.stripBraceAndQuotation(s);
                try {
                    String code = ctx().getCodeBuilder().addIncludes(s, lineNo);
                    return new CodeToken(code, ctx());
                } catch (NoClassDefFoundError e) {
                    raiseParseException("error adding includes: " + e.getMessage() + "\n possible cause: lower/upper case issue on windows platform");
                    return null;
                }
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.INCLUDE;
    }

}
