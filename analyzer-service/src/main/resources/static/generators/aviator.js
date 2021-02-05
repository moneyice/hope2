/**
 * @license
 * Copyright 2012 Google LLC
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * @fileoverview Helper functions for generating Aviator for blocks.
 * @author fraser@google.com (Neil Fraser)
 */
'use strict';

goog.provide('Blockly.Aviator');

goog.require('Blockly.Generator');
goog.require('Blockly.utils.global');
goog.require('Blockly.utils.string');


/**
 * Aviator code generator.
 * @type {!Blockly.Generator}
 */
Blockly.Aviator = new Blockly.Generator('Aviator');

/**
 * List of illegal variable names.
 * This is not intended to be a security feature.  Blockly is 100% client-side,
 * so bypassing this list is trivial.  This is intended to prevent users from
 * accidentally clobbering a built-in object or function.
 * @private
 */
Blockly.Aviator.addReservedWords(
  // https://developer.mozilla.org/en-US/docs/Web/Aviator/Reference/Lexical_grammar#Keywords
  'break,case,catch,class,const,continue,debugger,default,delete,do,else,export,extends,finally,for,function,if,import,in,instanceof,new,return,super,switch,this,throw,try,typeof,var,void,while,with,yield,' +
  'enum,' +
  'implements,interface,let,package,private,protected,public,static,' +
  'await,' +
  'null,true,false,' +
  // Magic variable.
  'arguments,' +
  // Everything in the current environment (835 items in Chrome, 104 in Node).
  Object.getOwnPropertyNames(Blockly.utils.global).join(','));

/**
 * Order of operation ENUMs.
 * https://developer.mozilla.org/en/Aviator/Reference/Operators/Operator_Precedence
 */
Blockly.Aviator.ORDER_ATOMIC = 0;           // 0 "" ...
Blockly.Aviator.ORDER_NEW = 1.1;            // new
Blockly.Aviator.ORDER_MEMBER = 1.2;         // . []
Blockly.Aviator.ORDER_FUNCTION_CALL = 2;    // ()
Blockly.Aviator.ORDER_INCREMENT = 3;        // ++
Blockly.Aviator.ORDER_DECREMENT = 3;        // --
Blockly.Aviator.ORDER_BITWISE_NOT = 4.1;    // ~
Blockly.Aviator.ORDER_UNARY_PLUS = 4.2;     // +
Blockly.Aviator.ORDER_UNARY_NEGATION = 4.3; // -
Blockly.Aviator.ORDER_LOGICAL_NOT = 4.4;    // !
Blockly.Aviator.ORDER_TYPEOF = 4.5;         // typeof
Blockly.Aviator.ORDER_VOID = 4.6;           // void
Blockly.Aviator.ORDER_DELETE = 4.7;         // delete
Blockly.Aviator.ORDER_AWAIT = 4.8;          // await
Blockly.Aviator.ORDER_EXPONENTIATION = 5.0; // **
Blockly.Aviator.ORDER_MULTIPLICATION = 5.1; // *
Blockly.Aviator.ORDER_DIVISION = 5.2;       // /
Blockly.Aviator.ORDER_MODULUS = 5.3;        // %
Blockly.Aviator.ORDER_SUBTRACTION = 6.1;    // -
Blockly.Aviator.ORDER_ADDITION = 6.2;       // +
Blockly.Aviator.ORDER_BITWISE_SHIFT = 7;    // << >> >>>
Blockly.Aviator.ORDER_RELATIONAL = 8;       // < <= > >=
Blockly.Aviator.ORDER_IN = 8;               // in
Blockly.Aviator.ORDER_INSTANCEOF = 8;       // instanceof
Blockly.Aviator.ORDER_EQUALITY = 9;         // == != === !==
Blockly.Aviator.ORDER_BITWISE_AND = 10;     // &
Blockly.Aviator.ORDER_BITWISE_XOR = 11;     // ^
Blockly.Aviator.ORDER_BITWISE_OR = 12;      // |
Blockly.Aviator.ORDER_LOGICAL_AND = 13;     // &&
Blockly.Aviator.ORDER_LOGICAL_OR = 14;      // ||
Blockly.Aviator.ORDER_CONDITIONAL = 15;     // ?:
Blockly.Aviator.ORDER_ASSIGNMENT = 16;      // = += -= **= *= /= %= <<= >>= ...
Blockly.Aviator.ORDER_YIELD = 17;           // yield
Blockly.Aviator.ORDER_COMMA = 18;           // ,
Blockly.Aviator.ORDER_NONE = 99;            // (...)

/**
 * List of outer-inner pairings that do NOT require parentheses.
 * @type {!Array.<!Array.<number>>}
 */
Blockly.Aviator.ORDER_OVERRIDES = [
  // (foo()).bar -> foo().bar
  // (foo())[0] -> foo()[0]
  [Blockly.Aviator.ORDER_FUNCTION_CALL, Blockly.Aviator.ORDER_MEMBER],
  // (foo())() -> foo()()
  [Blockly.Aviator.ORDER_FUNCTION_CALL, Blockly.Aviator.ORDER_FUNCTION_CALL],
  // (foo.bar).baz -> foo.bar.baz
  // (foo.bar)[0] -> foo.bar[0]
  // (foo[0]).bar -> foo[0].bar
  // (foo[0])[1] -> foo[0][1]
  [Blockly.Aviator.ORDER_MEMBER, Blockly.Aviator.ORDER_MEMBER],
  // (foo.bar)() -> foo.bar()
  // (foo[0])() -> foo[0]()
  [Blockly.Aviator.ORDER_MEMBER, Blockly.Aviator.ORDER_FUNCTION_CALL],

  // !(!foo) -> !!foo
  [Blockly.Aviator.ORDER_LOGICAL_NOT, Blockly.Aviator.ORDER_LOGICAL_NOT],
  // a * (b * c) -> a * b * c
  [Blockly.Aviator.ORDER_MULTIPLICATION, Blockly.Aviator.ORDER_MULTIPLICATION],
  // a + (b + c) -> a + b + c
  [Blockly.Aviator.ORDER_ADDITION, Blockly.Aviator.ORDER_ADDITION],
  // a && (b && c) -> a && b && c
  [Blockly.Aviator.ORDER_LOGICAL_AND, Blockly.Aviator.ORDER_LOGICAL_AND],
  // a || (b || c) -> a || b || c
  [Blockly.Aviator.ORDER_LOGICAL_OR, Blockly.Aviator.ORDER_LOGICAL_OR]
];

/**
 * Initialise the database of variable names.
 * @param {!Blockly.Workspace} workspace Workspace to generate code from.
 */
Blockly.Aviator.init = function (workspace) {
  // Create a dictionary of definitions to be printed before the code.
  Blockly.Aviator.definitions_ = Object.create(null);
  // Create a dictionary mapping desired function names in definitions_
  // to actual function names (to avoid collisions with user functions).
  Blockly.Aviator.functionNames_ = Object.create(null);

  if (!Blockly.Aviator.variableDB_) {
    Blockly.Aviator.variableDB_ =
      new Blockly.Names(Blockly.Aviator.RESERVED_WORDS_);
  } else {
    Blockly.Aviator.variableDB_.reset();
  }

  Blockly.Aviator.variableDB_.setVariableMap(workspace.getVariableMap());

  var defvars = [];
  // Add developer variables (not created or named by the user).
  var devVarList = Blockly.Variables.allDeveloperVariables(workspace);
  for (var i = 0; i < devVarList.length; i++) {
    defvars.push(Blockly.Aviator.variableDB_.getName(devVarList[i],
      Blockly.Names.DEVELOPER_VARIABLE_TYPE));
  }

  // Add user variables, but only ones that are being used.
  var variables = Blockly.Variables.allUsedVarModels(workspace);
  for (var i = 0; i < variables.length; i++) {
    defvars.push(Blockly.Aviator.variableDB_.getName(variables[i].getId(),
      Blockly.VARIABLE_CATEGORY_NAME));
  }

  // Declare all of the variables.
  if (defvars.length) {
    Blockly.Aviator.definitions_['variables'] ='';
  }
};

/**
 * Prepend the generated code with the variable definitions.
 * @param {string} code Generated code.
 * @return {string} Completed code.
 */
Blockly.Aviator.finish = function (code) {
  // Convert the definitions dictionary into a list.
  var definitions = [];
  for (var name in Blockly.Aviator.definitions_) {
    definitions.push(Blockly.Aviator.definitions_[name]);
  }
  // Clean up temporary data.
  delete Blockly.Aviator.definitions_;
  delete Blockly.Aviator.functionNames_;
  Blockly.Aviator.variableDB_.reset();
  return definitions.join('\n\n') + '\n\n' + code;
};

/**
 * Naked values are top-level blocks with outputs that aren't plugged into
 * anything.  A trailing semicolon is needed to make this legal.
 * @param {string} line Line of generated code.
 * @return {string} Legal line of code.
 */
Blockly.Aviator.scrubNakedValue = function (line) {
  return line + ';\n';
};

/**
 * Encode a string as a properly escaped Aviator string, complete with
 * quotes.
 * @param {string} string Text to encode.
 * @return {string} Aviator string.
 * @private
 */
Blockly.Aviator.quote_ = function (string) {
  // Can't use goog.string.quote since Google's style guide recommends
  // JS string literals use single quotes.
  string = string.replace(/\\/g, '\\\\')
    .replace(/\n/g, '\\\n')
    .replace(/'/g, '\\\'');
  return '\'' + string + '\'';
};

/**
 * Encode a string as a properly escaped multiline Aviator string, complete
 * with quotes.
 * @param {string} string Text to encode.
 * @return {string} Aviator string.
 * @private
 */
Blockly.Aviator.multiline_quote_ = function (string) {
  // Can't use goog.string.quote since Google's style guide recommends
  // JS string literals use single quotes.
  var lines = string.split(/\n/g).map(Blockly.Aviator.quote_);
  return lines.join(' + \'\\n\' +\n');
};

/**
 * Common tasks for generating Aviator from blocks.
 * Handles comments for the specified block and any connected value blocks.
 * Calls any statements following this block.
 * @param {!Blockly.Block} block The current block.
 * @param {string} code The Aviator code created for this block.
 * @param {boolean=} opt_thisOnly True to generate code for only this statement.
 * @return {string} Aviator code with comments and subsequent blocks added.
 * @private
 */
Blockly.Aviator.scrub_ = function (block, code, opt_thisOnly) {
  var commentCode = '';
  // Only collect comments for blocks that aren't inline.
  if (!block.outputConnection || !block.outputConnection.targetConnection) {
    // Collect comment for this block.
    var comment = block.getCommentText();
    if (comment) {
      comment = Blockly.utils.string.wrap(comment,
        Blockly.Aviator.COMMENT_WRAP - 3);
      commentCode += Blockly.Aviator.prefixLines(comment + '\n', '// ');
    }
    // Collect comments for all value arguments.
    // Don't collect comments for nested statements.
    for (var i = 0; i < block.inputList.length; i++) {
      if (block.inputList[i].type == Blockly.INPUT_VALUE) {
        var childBlock = block.inputList[i].connection.targetBlock();
        if (childBlock) {
          comment = Blockly.Aviator.allNestedComments(childBlock);
          if (comment) {
            commentCode += Blockly.Aviator.prefixLines(comment, '// ');
          }
        }
      }
    }
  }
  var nextBlock = block.nextConnection && block.nextConnection.targetBlock();
  var nextCode = opt_thisOnly ? '' : Blockly.Aviator.blockToCode(nextBlock);
  return commentCode + code + nextCode;
};

/**
 * Gets a property and adjusts the value while taking into account indexing.
 * @param {!Blockly.Block} block The block.
 * @param {string} atId The property ID of the element to get.
 * @param {number=} opt_delta Value to add.
 * @param {boolean=} opt_negate Whether to negate the value.
 * @param {number=} opt_order The highest order acting on this value.
 * @return {string|number}
 */
Blockly.Aviator.getAdjusted = function (block, atId, opt_delta, opt_negate,
  opt_order) {
  var delta = opt_delta || 0;
  var order = opt_order || Blockly.Aviator.ORDER_NONE;
  if (block.workspace.options.oneBasedIndex) {
    delta--;
  }
  var defaultAtIndex = block.workspace.options.oneBasedIndex ? '1' : '0';
  if (delta > 0) {
    var at = Blockly.Aviator.valueToCode(block, atId,
      Blockly.Aviator.ORDER_ADDITION) || defaultAtIndex;
  } else if (delta < 0) {
    var at = Blockly.Aviator.valueToCode(block, atId,
      Blockly.Aviator.ORDER_SUBTRACTION) || defaultAtIndex;
  } else if (opt_negate) {
    var at = Blockly.Aviator.valueToCode(block, atId,
      Blockly.Aviator.ORDER_UNARY_NEGATION) || defaultAtIndex;
  } else {
    var at = Blockly.Aviator.valueToCode(block, atId, order) ||
      defaultAtIndex;
  }

  if (Blockly.isNumber(at)) {
    // If the index is a naked number, adjust it right now.
    at = Number(at) + delta;
    if (opt_negate) {
      at = -at;
    }
  } else {
    // If the index is dynamic, adjust it in code.
    if (delta > 0) {
      at = at + ' + ' + delta;
      var innerOrder = Blockly.Aviator.ORDER_ADDITION;
    } else if (delta < 0) {
      at = at + ' - ' + -delta;
      var innerOrder = Blockly.Aviator.ORDER_SUBTRACTION;
    }
    if (opt_negate) {
      if (delta) {
        at = '-(' + at + ')';
      } else {
        at = '-' + at;
      }
      var innerOrder = Blockly.Aviator.ORDER_UNARY_NEGATION;
    }
    innerOrder = Math.floor(innerOrder);
    order = Math.floor(order);
    if (innerOrder && order >= innerOrder) {
      at = '(' + at + ')';
    }
  }
  return at;
};
