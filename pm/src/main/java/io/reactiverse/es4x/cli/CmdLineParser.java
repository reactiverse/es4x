/*
 * Copyright (c) 2001-2012 Steve Purcell.
 * Copyright (c) 2002      Vidar Holen.
 * Copyright (c) 2002      Michal Ceresna.
 * Copyright (c) 2005      Ewan Mellor.
 * Copyright (c) 2010-2012 penSec.IT UG (haftungsbeschränkt).
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the copyright holder nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package io.reactiverse.es4x.cli;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Largely GNU-compatible command-line options parser. Has short (-v) and
 * long-form (--verbose) option support, and also allows options with
 * associated values (-d 2, --debug 2, --debug=2). Option processing
 * can be explicitly terminated by the argument '--'.
 *
 * @author Steve Purcell
 * @author penSec.IT UG (haftungsbeschränkt)
 * @author Paulo Lopes
 *
 * @version 2.0
 */
public class CmdLineParser {

  /**
   * Base class for exceptions that may be thrown when options are parsed
   */
  public static abstract class OptionException extends Exception {
    OptionException(String msg) { super(msg); }
  }

  /**
   * Thrown when the parsed command-line contains an option that is not
   * recognised. {@code getMessage()} returns
   * an error string suitable for reporting the error to the user (in
   * English).
   */
  public static class UnknownOptionException extends OptionException {
    UnknownOptionException( String optionName ) {
      this(optionName, "Unknown option '" + optionName + "'");
    }

    UnknownOptionException( String optionName, String msg ) {
      super(msg);
      this.optionName = optionName;
    }

    /**
     * @return the name of the option that was unknown (e.g. "-u")
     */
    public String getOptionName() {
      return this.optionName;
    }

    private final String optionName;
  }

  /**
   * Thrown when the parsed commandline contains multiple concatenated
   * short options, such as -abcd, where one is unknown.
   * {@code getMessage()} returns an english human-readable error
   * string.
   * @author Vidar Holen
   */
  public static class UnknownSuboptionException
    extends UnknownOptionException {
    private char suboption;

    UnknownSuboptionException( String option, char suboption ) {
      super(option, "Illegal option: '"+suboption+"' in '"+option+"'");
      this.suboption=suboption;
    }
    public char getSuboption() {
      return suboption;
    }
  }

  /**
   * Thrown when the parsed commandline contains multiple concatenated
   * short options, such as -abcd, where one or more requires a value.
   * {@code getMessage()} returns an english human-readable error
   * string.
   * @author Vidar Holen
   */
  public static class NotFlagException extends UnknownOptionException {
    private char notflag;

    NotFlagException( String option, char unflaggish ) {
      super(option, "Illegal option: '"+option+"', '"+
        unflaggish+"' requires a value");
      notflag=unflaggish;
    }

    /**
     * @return the first character which wasn't a boolean (e.g 'c')
     */
    public char getOptionChar() {
      return notflag;
    }
  }

  /**
   * Thrown when an illegal or missing value is given by the user for
   * an option that takes a value. {@code getMessage()} returns
   * an error string suitable for reporting the error to the user (in
   * English).
   *
   * No generic class can ever extend {@code java.lang.Throwable}, so we
   * have to return {@code Option<?>} instead of
   * {@code Option<T>}.
   */
  public static class IllegalOptionValueException extends OptionException {
    public <T> IllegalOptionValueException( Option<T> opt, String value ) {
      super("Illegal value '" + value + "' for option " +
        (opt.shortForm() != null ? "-" + opt.shortForm() + "/" : "") +
        "--" + opt.longForm());
      this.option = opt;
      this.value = value;
    }

    /**
     * @return the name of the option whose value was illegal (e.g. "-u")
     */
    public Option<?> getOption() {
      return this.option;
    }

    /**
     * @return the illegal value
     */
    public String getValue() {
      return this.value;
    }
    private final Option<?> option;
    private final String value;
  }

  /**
   * Representation of a command-line option
   *
   * @param <T> Type of data configured by this option
   */
  public static abstract class Option<T> {

    protected Option( String longForm, boolean wantsValue ) {
      this(null, longForm, wantsValue);
    }

    protected Option( char shortForm, String longForm,
                      boolean wantsValue ) {
      this(new String(new char[]{shortForm}), longForm, wantsValue);
    }

    private Option( String shortForm, String longForm, boolean wantsValue ) {
      if ( longForm == null ) {
        throw new IllegalArgumentException("Null longForm not allowed");
      }
      this.shortForm = shortForm;
      this.longForm = longForm;
      this.wantsValue = wantsValue;
    }

    public String shortForm() {
      return this.shortForm;
    }

    public String longForm() {
      return this.longForm;
    }

    /**
     * Tells whether or not this option wants a value
     * @return true if the options wants a value
     */
    public boolean wantsValue() {
      return this.wantsValue;
    }

    public final T getValue( String arg, Locale locale )
      throws IllegalOptionValueException {
      if ( this.wantsValue ) {
        if ( arg == null ) {
          throw new IllegalOptionValueException(this, "");
        }
        return this.parseValue(arg, locale);
      } else {
        return this.getDefaultValue();
      }
    }

    /**
     * Override to extract and convert an option value passed on the
     * command-line
     * @param arg  the value
     * @param locale the locale used to parse
     * @return the parsed value
     * @throws IllegalOptionValueException if cannot parse
     */
    protected T parseValue(String arg, Locale locale)
      throws IllegalOptionValueException {

      return null;
    }

    /**
     * Override to define default value returned by getValue if option does
     * not want a value
     * @return the default value for the option (null)
     */
    protected T getDefaultValue() {
      return null;
    }

    private final String shortForm;
    private final String longForm;
    private final boolean wantsValue;



    /**
     * An option that expects a boolean value
     */
    public static class BooleanOption extends Option<Boolean> {
      public BooleanOption( char shortForm, String longForm ) {
        super(shortForm, longForm, false);
      }
      public BooleanOption( String longForm ) {
        super(longForm, false);
      }

      @Override
      public Boolean parseValue(String arg, Locale lcoale) {
        return Boolean.TRUE;
      }

      @Override
      public Boolean getDefaultValue() {
        return Boolean.TRUE;
      }
    }

    /**
     * An option that expects an integer value
     */
    public static class IntegerOption extends Option<Integer> {
      public IntegerOption( char shortForm, String longForm ) {
        super(shortForm, longForm, true);
      }
      public IntegerOption( String longForm ) {
        super(longForm, true);
      }

      @Override
      protected Integer parseValue( String arg, Locale locale )
        throws IllegalOptionValueException {
        try {
          return new Integer(arg);
        } catch (NumberFormatException e) {
          throw new IllegalOptionValueException(this, arg);
        }
      }
    }

    /**
     * An option that expects a long integer value
     */
    public static class LongOption extends Option<Long> {
      public LongOption( char shortForm, String longForm ) {
        super(shortForm, longForm, true);
      }
      public LongOption( String longForm ) {
        super(longForm, true);
      }

      @Override
      protected Long parseValue( String arg, Locale locale )
        throws IllegalOptionValueException {
        try {
          return new Long(arg);
        } catch (NumberFormatException e) {
          throw new IllegalOptionValueException(this, arg);
        }
      }
    }

    /**
     * An option that expects a floating-point value
     */
    public static class DoubleOption extends Option<Double> {
      public DoubleOption( char shortForm, String longForm ) {
        super(shortForm, longForm, true);
      }
      public DoubleOption( String longForm ) {
        super(longForm, true);
      }

      @Override
      protected Double parseValue( String arg, Locale locale )
        throws IllegalOptionValueException {
        try {
          NumberFormat format = NumberFormat.getNumberInstance(locale);
          Number num = format.parse(arg);
          return num.doubleValue();
        } catch (ParseException e) {
          throw new IllegalOptionValueException(this, arg);
        }
      }
    }

    /**
     * An option that expects a string value
     */
    public static class StringOption extends Option<String> {
      public StringOption( char shortForm, String longForm ) {
        super(shortForm, longForm, true);
      }
      public StringOption( String longForm ) {
        super(longForm, true);
      }

      @Override
      protected String parseValue( String arg, Locale locale ) {
        return arg;
      }
    }
  }

  /**
   * Add the specified Option to the list of accepted options
   * @param opt the option to add
   * @param <T> the kind of option
   * @return the opt param
   */
  public final <T> Option<T> addOption( Option<T> opt ) {
    if ( opt.shortForm() != null ) {
      this.options.put("-" + opt.shortForm(), opt);
    }
    this.options.put("--" + opt.longForm(), opt);
    return opt;
  }

  /**
   * Convenience method for adding a string option.
   * @param shortForm the short form character
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<String> addStringOption( char shortForm, String longForm ) {
    return addOption(new Option.StringOption(shortForm, longForm));
  }

  /**
   * Convenience method for adding a string option.
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<String> addStringOption( String longForm ) {
    return addOption(new Option.StringOption(longForm));
  }

  /**
   * Convenience method for adding an integer option.
   * @param shortForm the short form character
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Integer> addIntegerOption( char shortForm, String longForm ) {
    return addOption(new Option.IntegerOption(shortForm, longForm));
  }

  /**
   * Convenience method for adding an integer option.
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Integer> addIntegerOption( String longForm ) {
    return addOption(new Option.IntegerOption(longForm));
  }

  /**
   * Convenience method for adding a long integer option.
   * @param shortForm the short form character
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Long> addLongOption( char shortForm, String longForm ) {
    return addOption(new Option.LongOption(shortForm, longForm));
  }

  /**
   * Convenience method for adding a long integer option.
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Long> addLongOption( String longForm ) {
    return addOption(new Option.LongOption(longForm));
  }

  /**
   * Convenience method for adding a double option.
   * @param shortForm the short form character
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Double> addDoubleOption( char shortForm, String longForm ) {
    return addOption(new Option.DoubleOption(shortForm, longForm));
  }

  /**
   * Convenience method for adding a double option.
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Double> addDoubleOption( String longForm ) {
    return addOption(new Option.DoubleOption(longForm));
  }

  /**
   * Convenience method for adding a boolean option.
   * @param shortForm the short form character
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Boolean> addBooleanOption( char shortForm, String longForm ) {
    return addOption(new Option.BooleanOption(shortForm, longForm));
  }

  /**
   * Convenience method for adding a boolean option.
   * @param longForm the long form string
   * @return the new Option
   */
  public final Option<Boolean> addBooleanOption( String longForm ) {
    return addOption(new Option.BooleanOption(longForm));
  }

  /**
   * Equivalent to {@link #getOptionValue(Option, Object) getOptionValue(o,
   * null)}.
   * @param o the option to extract the value
   * @param <T> the kind of option
   * @return the value or null
   */
  public final <T> T getOptionValue( Option<T> o ) {
    return getOptionValue(o, null);
  }


  /**
   * @param o the option to extract the value
   * @param def the default value if vallue is null or empty
   * @param <T> the kind of option
   * @return the parsed value of the given Option, or the given default 'def'
   * if the option was not set
   */
  public final <T> T getOptionValue( Option<T> o, T def ) {
    List<?> v = values.get(o.longForm());

    if (v == null) {
      return def;
    } else if (v.isEmpty()) {
      return null;
    } else {

      /* Cast should be safe because Option.parseValue has to return an
       * instance of type T or null
       */
      @SuppressWarnings("unchecked")
      T result = (T)v.remove(0);
      return result;
    }
  }


  /**
   * @param option the option to extract the value
   * @param <T> the kind of option
   * @return A Collection giving the parsed values of all the occurrences of
   * the given Option, or an empty Collection if the option was not set.
   */
  public final <T> Collection<T> getOptionValues(Option<T> option) {
    Collection<T> result = new ArrayList<>();

    while (true) {
      T o = getOptionValue(option, null);

      if (o == null) {
        return result;
      } else {
        result.add(o);
      }
    }
  }


  /**
   * @return the non-option arguments
   */
  public final String[] getRemainingArgs() {
    return this.remainingArgs;
  }

  /**
   * Extract the options and non-option arguments from the given
   * list of command-line arguments. The default locale is used for
   * parsing options whose values might be locale-specific.
   *
   * @param argv the command line arguments to parse
   * @throws OptionException if cannot parse the options
   */
  public final void parse( String[] argv ) throws OptionException {
    parse(argv, Locale.getDefault());
  }

  /**
   * Extract the options and non-option arguments from the given
   * list of command-line arguments. The specified locale is used for
   * parsing options whose values might be locale-specific.
   *
   * @param argv the command line arguments to parse
   * @param locale the locale to use during the parsing
   * @throws OptionException if cannot parse the options
   */
  public final void parse( String[] argv, Locale locale )
    throws OptionException {

    ArrayList<Object> otherArgs = new ArrayList<>();
    int position = 0;
    this.values = new HashMap<>(10);
    while ( position < argv.length ) {
      String curArg = argv[position];
      if ( curArg.startsWith("-") ) {
        if ( curArg.equals("--") ) { // end of options
          position += 1;
          break;
        }
        String valueArg = null;
        if ( curArg.startsWith("--") ) { // handle --arg=value
          int equalsPos = curArg.indexOf("=");
          if ( equalsPos != -1 ) {
            valueArg = curArg.substring(equalsPos+1);
            curArg = curArg.substring(0,equalsPos);
          }
        } else if(curArg.length() > 2) {  // handle -abcd
          for(int i=1; i<curArg.length(); i++) {
            Option<?> opt=this.options.get("-"+curArg.charAt(i));
            if(opt==null) {
              throw new UnknownSuboptionException(curArg,curArg.charAt(i));
            }
            if(opt.wantsValue()) {
              throw new NotFlagException(curArg,curArg.charAt(i));
            }
            addValue(opt, null, locale);

          }
          position++;
          continue;
        }

        Option<?> opt = this.options.get(curArg);
        if ( opt == null ) {
          throw new UnknownOptionException(curArg);
        }

        if ( opt.wantsValue() ) {
          if ( valueArg == null ) {
            position += 1;
            if ( position < argv.length ) {
              valueArg = argv[position];
            }
          }
          addValue(opt, valueArg, locale);
        } else {
          addValue(opt, null, locale);
        }

        position += 1;
      }
      else {
        otherArgs.add(curArg);
        position += 1;
      }
    }
    for ( ; position < argv.length; ++position ) {
      otherArgs.add(argv[position]);
    }

    this.remainingArgs = new String[otherArgs.size()];
    remainingArgs = otherArgs.toArray(remainingArgs);
  }


  private <T> void addValue(Option<T> opt, String valueArg, Locale locale)
    throws IllegalOptionValueException {

    T value = opt.getValue(valueArg, locale);
    String lf = opt.longForm();

    /* Cast is typesafe because the only location we add elements to the
     * values map is in this method.
     */
    @SuppressWarnings("unchecked")
    List<T> v = (List<T>) values.get(lf);

    if (v == null) {
      v = new ArrayList<>();
      values.put(lf, v);
    }

    v.add(value);
  }


  private String[] remainingArgs = null;
  private Map<String, Option<?>> options = new HashMap<>(10);
  private Map<String, List<?>> values = new HashMap<>(10);
}
