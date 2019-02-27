/*
* 设计器私有的配置说明 
* 一
* UE.shfbFormDesignUrl  插件路径
* 
* 二
*UE.getEditor('myFormDesign',{
*          toolshfb:true,//是否显示，设计器的清单 tool
*/
UE.shfbFormDesignUrl = 'formdesign';
/**
 * 文本框
 * @command textfield
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'textfield');
 * ```
 */
UE.plugins['text'] = function () {
	var me = this,thePlugins = 'text';
	me.commands[thePlugins] = {
		execCommand:function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl:this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl+'/shfbHTML/text.html',
				name:thePlugins,
				editor:this,
				title: '文本框',
				cssRules:"width:600px;height:310px;",
				buttons:[
				{
					className:'edui-okbutton',
					label:'确定',
					onclick:function () {
						dialog.close(true);
					}
				},
				{
					className:'edui-cancelbutton',
					label:'取消',
					onclick:function () {
						dialog.close(false);
					}
				}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup( {
		editor:this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			  baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			  me.execCommand(thePlugins);
			  this.hide();
		},
		_delete:function(){
			if( window.confirm('确认删除该控件吗？') ) {
				baidu.editor.dom.domUtils.remove(this.anchorEl,false);
			}
			this.hide();
		}
	} );
	popup.render();
	me.addListener( 'mouseover', function( t, evt ) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
        var shfbPlugins = el.getAttribute('type');
		if ( /input/ig.test( el.tagName ) && shfbPlugins==thePlugins) {
			var html = popup.formatHtml(
				'<nobr>文本框: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>' );
			if ( html ) {
				popup.getDom( 'content' ).innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor( popup.anchorEl );
			} else {
				popup.hide();
			}
		}
	});
};
/**
 * 多行文本框
 * @command textarea
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'textarea');
 * ```
 */
UE.plugins['textarea'] = function () {
	var me = this, thePlugins = 'textarea';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/textarea.html',
				name: thePlugins,
				editor: this,
				title: '多行文本框',
				cssRules: "width:600px;height:330px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		if (/textarea/ig.test(el.tagName)) {
			var html = popup.formatHtml(
				'<nobr>多行文本框: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(popup.anchorEl);
			} else {
				popup.hide();
			}
		}
	});
};
/**
 * 单选框组
 * @command radios
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'radio');
 * ```
 */
UE.plugins['radio'] = function () {
	var me = this, thePlugins = 'radio';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/radio.html',
				name: thePlugins,
				editor: this,
				title: '单选框组',
				cssRules: "width:800px;height:450px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var shfbPlugins = el.getAttribute('shfbPlugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>单选框组: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};
/**
 * 复选框组
 * @command checkbox
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'checkbox');
 * ```
 */
UE.plugins['checkbox'] = function () {
	var me = this, thePlugins = 'checkbox';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/checkbox.html',
				name: thePlugins,
				editor: this,
				title: '复选框组',
				cssRules: "width:800px;height:400px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var shfbPlugins = el.getAttribute('shfbplugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>复选框组: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};
/**
 * 下拉菜单
 * @command select
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'select');
 * ```
 */
UE.plugins['select'] = function () {
	var me = this, thePlugins = 'select';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/select.html',
				name: thePlugins,
				editor: this,
				title: '下拉菜单',
				cssRules: "width:750px;height:400px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var shfbPlugins = el.getAttribute('shfbplugins');
		if (/select|span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>下拉菜单: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				if (el.tagName == 'SPAN') {
					var elInput = el.getElementsByTagName("select");
					el = elInput.length > 0 ? elInput[0] : el;
				}
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(popup.anchorEl);
			} else {
				popup.hide();
			}
		}
	});

};
///**
//* oframe控件
//* @method execCommand
//* @param { String } cmd 命令字符串
//* @example
//*/
//UE.plugins['oframeplugins'] = function () {
//	var me = this, thePlugins = 'oframeplugins';
//	me.commands[thePlugins] = {
//		execCommand: function () {
//			var dialog = new UE.ui.Dialog({
//				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/oframeplugin.html',
//				name: thePlugins,
//				editor: this,
//				title: 'oframe控件',
//				cssRules: "width:800px;height:400px;",
//				buttons: [
//					{
//						className: 'edui-okbutton',
//						label: '确定',
//						onclick: function () {
//							dialog.close(true);
//						}
//					},
//					{
//						className: 'edui-cancelbutton',
//						label: '取消',
//						onclick: function () {
//							dialog.close(false);
//						}
//					}]
//			});
//			dialog.render();
//			dialog.open();
//		}
//	};
//	var popup = new baidu.editor.ui.Popup({
//		editor: this,
//		content: '',
//		className: 'edui-bubble',
//		_edittext: function () {
//			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
//			me.execCommand(thePlugins);
//			this.hide();
//		},
//		_delete: function () {
//			if (window.confirm('确认删除该控件吗？')) {
//				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
//			}
//			this.hide();
//		}
//	});
//	popup.render();
//	me.addListener('mouseover', function (t, evt) {
//		evt = evt || window.event;
//		var el = evt.target || evt.srcElement;
//		var shfbPlugins = el.getAttribute('oframeplugins');
//		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
//			var html = popup.formatHtml(
//				'<nobr>oframe控件编辑: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
//			if (html) {
//				var elInput = el.getElementsByTagName("input");
//				var rEl = elInput.length > 0 ? elInput[0] : el;
//				popup.getDom('content').innerHTML = html;
//				popup.anchorEl = el;
//				popup.showAnchor(rEl);
//			} else {
//				popup.hide();
//			}
//		}
//	});
//};
/**
* oframe控件  input
* @method execCommand
* @param { String } cmd 命令字符串
*/
UE.plugins['oframeinput'] = function () {
	var me = this, thePlugins = 'oframeinput';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/oframeinput.html',
				name: thePlugins,
				editor: this,
				title: '域文本框',
				cssRules: "width:1000px;height:450px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var $el = $(el);
		if($el.hasClass("oframecover")) {
			el = $el.parent()[0];
		}
		var shfbPlugins = el.getAttribute('oframeplugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>域文本框: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};

/**
 * oframe控件  checkbox
 * @method execCommand
 * @param { String } cmd 命令字符串
 */
UE.plugins['oframecheckbox'] = function () {
	var me = this, thePlugins = 'oframecheckbox';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/oframecheckbox.html',
				name: thePlugins,
				editor: this,
				title: '域复选框',
				cssRules: "width:1000px;height:450px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var $el = $(el);
		if ($el.hasClass("oframecover")) {
			el = $el.parent()[0];
		}
		var shfbPlugins = el.getAttribute('oframeplugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>域复选框: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};

/**
 * oframe控件  include
 * @method execCommand
 * @param { String } cmd 命令字符串
 */
UE.plugins['oframeinclude'] = function () {
	var me = this, thePlugins = 'oframeinclude';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/oframeinclude.html',
				name: thePlugins,
				editor: this,
				title: '域引用控件',
				cssRules: "width:1000px;height:450px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var $el = $(el);
		if ($el.hasClass("oframecover")) {
			el = $el.parent()[0];
		}
		var shfbPlugins = el.getAttribute('oframeplugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>域引用控件: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};

/**
 * oframe控件  radio
 * @method execCommand
 * @param { String } cmd 命令字符串
 */
UE.plugins['oframeradio'] = function () {
	var me = this, thePlugins = 'oframeradio';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/oframeradio.html',
				name: thePlugins,
				editor: this,
				title: '域单选框',
				cssRules: "width:1000px;height:450px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var $el = $(el);
		if ($el.hasClass("oframecover")) {
			el = $el.parent()[0];
		}
		var shfbPlugins = el.getAttribute('oframeplugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>域单选框: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};

/**
 * oframe控件  select
 * @method execCommand
 * @param { String } cmd 命令字符串
 */
UE.plugins['oframeselect'] = function () {
	var me = this, thePlugins = 'oframeselect';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/oframeselect.html',
				name: thePlugins,
				editor: this,
				title: '域下拉菜单',
				cssRules: "width:1000px;height:450px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var $el = $(el);
		if ($el.hasClass("oframecover")) {
			el = $el.parent()[0];
		}
		var shfbPlugins = el.getAttribute('oframeplugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>域下拉菜单: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};

/**
 * oframe控件  textarea
 * @method execCommand
 * @param { String } cmd 命令字符串
 */
UE.plugins['oframetextarea'] = function () {
	var me = this, thePlugins = 'oframetextarea';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfbHTML/oframetextarea.html',
				name: thePlugins,
				editor: this,
				title: '域多行输入框',
				cssRules: "width:1000px;height:450px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					},
					{
						className: 'edui-cancelbutton',
						label: '取消',
						onclick: function () {
							dialog.close(false);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
	var popup = new baidu.editor.ui.Popup({
		editor: this,
		content: '',
		className: 'edui-bubble',
		_edittext: function () {
			baidu.editor.plugins[thePlugins].editdom = popup.anchorEl;
			me.execCommand(thePlugins);
			this.hide();
		},
		_delete: function () {
			if (window.confirm('确认删除该控件吗？')) {
				baidu.editor.dom.domUtils.remove(this.anchorEl, false);
			}
			this.hide();
		}
	});
	popup.render();
	me.addListener('mouseover', function (t, evt) {
		evt = evt || window.event;
		var el = evt.target || evt.srcElement;
		var $el = $(el);
		if ($el.hasClass("oframecover")) {
			el = $el.parent()[0];
		}
		var shfbPlugins = el.getAttribute('oframeplugins');
		if (/span/ig.test(el.tagName) && shfbPlugins == thePlugins) {
			var html = popup.formatHtml(
				'<nobr>域多行输入框: <span onclick=$$._edittext() class="edui-clickable">编辑</span>&nbsp;&nbsp;<span onclick=$$._delete() class="edui-clickable">删除</span></nobr>');
			if (html) {
				var elInput = el.getElementsByTagName("input");
				var rEl = elInput.length > 0 ? elInput[0] : el;
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = el;
				popup.showAnchor(rEl);
			} else {
				popup.hide();
			}
		}
	});
};

UE.plugins['shfb'] = function () {
	var me = this, thePlugins = 'shfb';
	me.commands[thePlugins] = {
		execCommand: function () {
			var dialog = new UE.ui.Dialog({
				iframeUrl: this.options.UEDITOR_HOME_URL + UE.shfbFormDesignUrl + '/shfb.html',
				name: thePlugins,
				editor: this,
				title: '表单设计器 - 清单',
				cssRules: "width:620px;height:250px;",
				buttons: [
					{
						className: 'edui-okbutton',
						label: '确定',
						onclick: function () {
							dialog.close(true);
						}
					}]
			});
			dialog.render();
			dialog.open();
		}
	};
};

UE.registerUI('button_shfb', function (editor, uiName) {
	if (!this.options.toolshfb) {
		return false;
	}
	//注册按钮执行时的command命令，使用命令默认就会带有回退操作
	editor.registerCommand(uiName, {
		execCommand: function () {
			editor.execCommand('shfb');
		}
	});
	//创建一个button
	var btn = new UE.ui.Button({
		//按钮的名字
		name: uiName,
		//提示
		title: "表单设计器",
		//需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
		cssRules: 'background-position: -401px -40px;',
		//点击时执行的命令
		onclick: function () {
			//这里可以不用执行命令,做你自己的操作也可
			editor.execCommand(uiName);
		}
	});
	/*
	 //当点到编辑内容上时，按钮要做的状态反射
	 editor.addListener('selectionchange', function () {
	 var state = editor.queryCommandState(uiName);
	 if (state == -1) {
	 btn.setDisabled(true);
	 btn.setChecked(false);
	 } else {
	 btn.setDisabled(false);
	 btn.setChecked(state);
	 }
	 });
	 */
	//因为你是添加button,所以需要返回这个button
	return btn;
});


