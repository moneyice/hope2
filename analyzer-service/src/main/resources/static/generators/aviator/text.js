/**
 * @license
 * Copyright 2012 Google LLC
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * @fileoverview Generating Aviator for text blocks.
 * @author fraser@google.com (Neil Fraser)
 */
'use strict';

goog.provide('Blockly.Aviator.texts');

goog.require('Blockly.Aviator');


Blockly.Aviator['text'] = function (block) {
  // Text value.
  var code = Blockly.Aviator.quote_(block.getFieldValue('TEXT'));
  return [code, Blockly.Aviator.ORDER_ATOMIC];
};

/**
 * Enclose the provided value in 'String(...)' function.
 * Leave string literals alone.
 * @param {string} value Code evaluating to a value.
 * @return {string} Code evaluating to a string.
 * @private
 */
Blockly.Aviator.text.forceString_ = function (value) {
  if (Blockly.Aviator.text.forceString_.strRegExp.test(value)) {
    return value;
  }
  return 'String(' + value + ')';
};

/**
 * Regular expression to detect a single-quoted string literal.
 */
Blockly.Aviator.text.forceString_.strRegExp = /^\s*'([^']|\\')*'\s*$/;


Blockly.Aviator['text_length'] = function (block) {
  // String or array length.
  var text = Blockly.Aviator.valueToCode(block, 'VALUE',
    Blockly.Aviator.ORDER_FUNCTION_CALL) || '\'\'';
  return [text + '.length', Blockly.Aviator.ORDER_MEMBER];
};

Blockly.Aviator['text_isEmpty'] = function (block) {
  // Is the string null or array empty?
  var text = Blockly.Aviator.valueToCode(block, 'VALUE',
    Blockly.Aviator.ORDER_MEMBER) || '\'\'';
  return ['!' + text + '.length', Blockly.Aviator.ORDER_LOGICAL_NOT];
};

