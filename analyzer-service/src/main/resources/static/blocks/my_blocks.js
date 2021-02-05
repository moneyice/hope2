    ////设置my 自定义块
    Blockly.Blocks['my_return'] = {
        init: function () {
          this.appendValueInput('VALUE')
            .setCheck('Number')
            .appendField('返回', 'NAME');
          this.setPreviousStatement(true, 'Number');
          // this.setOutput(true, 'Number');
          this.setColour(210);
          this.setTooltip('返回');
          this.setHelpUrl('return');
        }
      };
      //my-function   .appendField('getHome');
      Blockly.Blocks['my_function'] = {
        init: function () {
          this.appendDummyInput()
            .setAlign(Blockly.ALIGN_RIGHT)
            .appendField(new Blockly.FieldLabelSerializable(''), 'NAME');
          this.setOutput(true, 'Number');
          this.setColour("#FF9900");
          this.setTooltip('');
          this.setHelpUrl('');
        }
      }
      //带一个参数的函数  块样式配置
      Blockly.Blocks['my_function_1'] = {
        init: function () {
          this.appendValueInput("FIRST")
            .setCheck(null)
            .appendField(new Blockly.FieldLabelSerializable(""), "NAME");
          this.appendDummyInput();
          this.setOutput(true, null);
          this.setColour("#993300");
          this.setTooltip("这是带一个参数的函数");
          this.setHelpUrl("");
        }
      };