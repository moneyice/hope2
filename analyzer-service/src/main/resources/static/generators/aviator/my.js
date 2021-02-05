/**
 * @license
 * Copyright 2012 Google LLC
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * @fileoverview Generating Aviator for colour blocks.
 * @author fraser@google.com (Neil Fraser)
 */
'use strict';

goog.provide('Blockly.Aviator.mys');

goog.require('Blockly.Aviator');

var data = getFuncData();
var funclist = data[0].allData.concat(data[1].allData);
var funclists = [];
var funclists_1 = [];
for (var index = 0; index < funclist.length; index++) {
  if (funclist[index].paramNum == '0') {
    funclists.push(funclist[index])
  } else if (funclist[index].paramNum == '1') {
    funclists_1.push(funclist[index])
  }
}
//不带参数的处理模块
Blockly.Aviator['my_function'] = function (block) {
  var funcName = block.getFieldValue('NAME')
  var code;
  for (var i = 0; i < funclists.length + 1; i++) {
    if (funcName == funclists[i].name && funclists[i].customFlag == 'N') {
      funcName = funclists[i].code;
      code = [funcName + '()', Blockly.Aviator.ORDER_FUNCTION_CALL];
      break;
    } else if (funcName == funclists[i].name && funclists[i].customFlag == 'Y') {
      var param1 = funclists[i].code;
      var param2 = funclists[i].defaultType;
      var param3 = funclists[i].orgId;
      code = ['customFunction' + '(' + param1 + ',' + param2 + ',' + param3 + ')', Blockly.Aviator.ORDER_FUNCTION_CALL];
      break;
    } else if (funcName == funclists[i].name && funclists[i].customFlag == 'subject') {
      var param1 = funclists[i].code;
      var param2 = funclists[i].effecttiveDate;
      code = ['subjectFunction' + '(' + param1 + ',' + param2 + ')', Blockly.Aviator.ORDER_FUNCTION_CALL];
      break;
    }
  }
  this.setTooltip("nihao")
  console.log(this);
  return code;
};

//带1个参数的处理模块
Blockly.Aviator['my_function_1'] = function (block) {
  // Define a procedure with a return value.
  var funcName = block.getFieldValue('NAME')
  for (var i = 0; i < funclists_1.length; i++) {
    if (funcName == funclists_1[i].name) {
      funcName = funclists_1[i].code;
    }
  }
  var argument0 = Blockly.Aviator.valueToCode(block, 'FIRST',
    Blockly.Aviator.ORDER_MODULUS) || '0';
  var code = [funcName + '(' + argument0 + ')', Blockly.Aviator.ORDER_FUNCTION_CALL]
  return code;
};

/**
* Construct the blocks required by the flyout for the colours category.
* @param {!Blockly.Workspace} workspace The workspace this flyout is for.
* @return {!Array.<!Element>} Array of XML block elements.
*/

