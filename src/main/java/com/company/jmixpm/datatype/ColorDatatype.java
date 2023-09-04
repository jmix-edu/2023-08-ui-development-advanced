package com.company.jmixpm.datatype;

import com.google.common.base.Strings;
import io.jmix.core.metamodel.annotation.DatatypeDef;
import io.jmix.core.metamodel.annotation.Ddl;
import io.jmix.core.metamodel.datatype.Datatype;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DatatypeDef(
        id="color",
        javaClass = String.class
)
@Ddl("varchar(6)")
public class ColorDatatype implements Datatype<String> {

    private static final Pattern PATTERN = Pattern.compile("^#?(?<color>[a-fA-F0-9]{6})$");

    @Override
    public String format(@Nullable Object value) {
        if (value == null) {
            return "";
        }

        String color = String.valueOf(value).toUpperCase();
        return color.startsWith("#") ? color : "#" + color;
    }

    @Override
    public String format(@Nullable Object value, Locale locale) {
        return format(value);
    }

    @Nullable
    @Override
    public String parse(@Nullable String value) throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new ParseException(String.format("Unparseable color: \"%s\"", value), 0);
        }

        return matcher.group("color");
    }

    @Nullable
    @Override
    public String parse(@Nullable String value, Locale locale) throws ParseException {
        return parse(value);
    }
}
