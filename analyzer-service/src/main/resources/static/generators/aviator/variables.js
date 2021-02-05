/**
 * @license
 * Copyright 2012 Google LLC
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * @fileoverview Generating Aviator for variable blocks.
 * @author fraser@google.com (Neil Fraser)
 */
'use strict';

goog.provide('Blockly.Aviator.variables');

goog.require('Blockly.Aviator');

//获得MyData
var VariablesData = getVariablesData();

Blockly.Aviator['variables_get'] = function (block) {
  //  获取变量名字 
  var variablesName = Blockly.Aviator.variableDB_.getName(block.getFieldValue('VAR'),
    Blockly.VARIABLE_CATEGORY_NAME);
  console.log('dow');
  var noVar = false;
  // 判断名字是否是自定参中所带  是则转换成英文昵称输出
  for (let i = 0; i < VariablesData.length; i++) {
    if (variablesName == VariablesData[i][0]) {
      variablesName = VariablesData[i][1];
      //循环完未发现自定参数中有此名 
    } else if (i == VariablesData.length - 1) {
      noVar = true;
    };
  };
  //判断若参数名不是自定参数所带 转换成汉语拼音输出
  if (noVar) {
    variablesName = pinyin.getFullChars(variablesName);
  }

  return [variablesName, Blockly.Aviator.ORDER_ATOMIC];
};

Blockly.Aviator['variables_set'] = function (block) {
  // 获取后边带的参数  若未带参默认为0
  var argument0 = Blockly.Aviator.valueToCode(block, 'VALUE',
    Blockly.Aviator.ORDER_ASSIGNMENT) || '0';
  // 获取变量名字 
  var variablesName = Blockly.Aviator.variableDB_.getName(
    block.getFieldValue('VAR'), Blockly.VARIABLE_CATEGORY_NAME);
  var noName = false;
  // 判断名字是否是自定参中所带  是则转换成英文昵称输出
  for (let i = 0; i < VariablesData.length; i++) {
    if (variablesName == VariablesData[i][0]) {
      variablesName = VariablesData[i][1];
      //循环完未发现自定参数中有此名 
    } else if (i == VariablesData.length - 1) {
      noName = true;
    };
  };
  //判断若参数名不是自定参数所带 转换成汉语拼音输出
  if (noName) {
    variablesName = pinyin.getFullChars(variablesName);
  }

  // variablesName = pinyin.getFullChars(variablesName);
  return variablesName + ' = ' + argument0 + ';\n';
};
