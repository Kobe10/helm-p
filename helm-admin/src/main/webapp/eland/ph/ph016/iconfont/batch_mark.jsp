<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/4/10
  Time: 16:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<style>
    .sign-box {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        position: relative;
    }

    .sign-box .panelBar {
        background-color: #fff;
        border: none;
    }

    .sign-selected--title {
        background-color: #F2F2F2;
        height: 28px;
        line-height: 28px;
        font-size: 12px;
        text-indent: 10px;
        color: #3e90c8;
        font-weight: bold;
        border-bottom: 1px solid #ddd;
        border-top: 1px solid #ddd;
    }

    .sign-selected--content {
        flex: 1;
        border-bottom: 1px solid #ddd;
        padding-bottom: 10px;
        padding-right: 10px;
        overflow-y: auto
    }

    .sign-selected--label {
        margin-top: 10px;
        margin-left: 10px;
        display: inline-block;
        padding: 1px 5px;
        border: 1px solid #3e90c8;
        border-radius: 3px;
        color: #333333;
        background-color: #ccffff;
    }

    .sign-selected--label span {
        font-size: 12px;
        color: #333333;
    }

    .sign-selected--label-delete {

    }

    /*------标签新增----------*/

    .sign-add {
        flex: 2;
        display: flex;
        flex-direction: column;
    }

    .sign-add--title {
        height: 28px;
        line-height: 28px;
        display: flex;
    }

    .sign-add--content {
        flex: 1;
        display: flex;
    }

    .sign-private--title, .sign-system--title {
        flex: 1;
        border-bottom: 1px solid #ddd;
        text-align: center;
    }

    .sign-private--title, .sign-private--content {
        border-right: 1px solid #ddd;
    }

    .sign-selected--content::-webkit-scrollbar, .sign-private--content::-webkit-scrollbar, .sign-system--content::-webkit-scrollbar {
        width: 3px;
    }

    .sign-selected--content::-webkit-scrollbar-thumb, .sign-private--content::-webkit-scrollbar-thumb, .sign-system--content::-webkit-scrollbar-thumb { /*滚动条里面小方块*/
        border-radius: 3px;
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        background: #535353;
    }

    .sign-selected--content::-webkit-scrollbar-track, .sign-private--content::-webkit-scrollbar-track, .sign-system--content::-webkit-scrollbar-track { /*滚动条里面轨道*/
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        border-radius: 3px;
        background: #EDEDED;
    }

    .sign-private--content, .sign-system--content {
        flex: 1;
        padding-bottom: 10px;
        padding-right: 10px;
        overflow-y: auto;
    }

    .sign-private--label, sign-system--label {
        margin-top: 10px;
        margin-left: 10px;
        width: 114px;
        height: auto;
        display: inline-block;
        vertical-align: top;
        text-align: center;
        border: 1px solid #666;
        cursor: pointer;
        position: relative;
    }

    .sign--label-key {
        width: 100%;
        height: 28px;
        line-height: 28px;
        transition: background-color 0.7s, color 0.7s;
        -moz-transition: background-color 0.7s, color 0.7s; /* Firefox 4 */
        -webkit-transition: background-color 0.7s, color 0.7s; /* Safari 和 Chrome */
        -o-transition: background-color 0.7s, color 0.7s; /* Opera */
    }

    .sign--label-value {
        width: 100%;
        height: auto;
        background-color: #ffffff;
        border-left: 1px solid #666;
        border-right: 1px solid #666;
        position: absolute;
        z-index: 999;
        left: -1px;
    }

    .to-up {
        width: 100%;
        height: auto;
        background-color: #ffffff;
        border-left: 1px solid #666;
        border-right: 1px solid #666;
        position: absolute;
        z-index: 999;
        bottom: 28px;
        left: -1px;
    }

    .sign--label-value li {
        width: 100%;
        height: 0px;
        line-height: 28px;
        cursor: pointer;
        overflow: hidden;
        transition: height 0.7s, background-color 0.7s, color 0.7s;
        -moz-transition: height 0.7s, background-color 0.7s, color 0.7s; /* Firefox 4 */
        -webkit-transition: height 0.7s, background-color 0.7s, color 0.7s; /* Safari 和 Chrome */
        -o-transition: height 0.7s, background-color 0.7s, color 0.7s; /* Opera */
    }

    .sign--label-value li:hover, .sign-private--label:hover .sign--label-key, .sign-private--label:hover .sign--label-key {
        background-color: #3e90c8;
        color: #fff;
    }

    .sign-private--label:hover .sign--label-value, sign-system--label:hover .sign--label-value {
        border-bott: 1px solid #666;
    }

    .sign-private--label:hover .sign--label-value li, sign-system--label:hover .sign--label-value li {
        height: 28px;
        border-top: 1px solid #666;
    }

    .sign-private--label:hover .sign--label-edit, sign-system--label:hover .sign--label-edit {
        border-bottom: 1px solid #666;
    }

    .sign--label-edit {
        background-color: #fff !important;
        color: #3e90c8 !important;
        text-decoration: underline !important;
    }

    .sign--label-value li.active {
        background-color: #3e90c8;
        color: #fff;
    }

    .sign-add-btn {
        color: #3e90c8;
        cursor: pointer;
    }

    .icon {
        width: 1em;
        height: 1em;
        vertical-align: -0.15em;
        fill: currentColor;
        overflow: hidden;
    }

    /*--------弹框-------------*/
    .sign-dialog-box {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        text-align: center;
        display: none;
    }

    .sign-dialog {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        width: 480px;
        height: 300px;
    }

    .sign-single--box {
        display: flex;
        width: 100%;
        height: 28px;
        line-height: 28px;
        border-top: 1px solid #ddd;
    }

    .sign-single--box-title {
        flex: 1;
        border-right: 1px solid #ddd;
        text-align: right;
        padding-right: 5px;
    }

    .border-left-ddd {
        border-left: 1px solid #ddd;
    }

    .sign-single--box-show {
        flex: 3;
    }
</style>
<div class="sign-box">
    <div class="panelBar ">
        <ul class="toolBar">
            <li onclick=""><a class="add"><span>绑定</span></a></li>
            <li onclick=""><a class="delete"><span>移除</span></a></li>
            <li onclick=""><a class="save"><span>保存</span></a></li>
            <li onclick="$.pdialog.closeCurrent();"><a class="close"><span>关闭</span></a></li>
        </ul>
    </div>
    <div class="sign-single--box">
        <div class="sign-single--box-title">档案编号:</div>
        <div class="sign-single--box-show"></div>
        <div class="sign-single--box-title border-left-ddd">被安置人:</div>
        <div class="sign-single--box-show"></div>
    </div>
    <div class="sign-selected--title"><span>已选标签</span></div>
    <div class="sign-selected--content">
        <!--单户操作时循环生成已选标签-->
        <div class="sign-selected--label">
            <span class="">资料核实:</span>
            <span class="">已核实</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <div class="sign-selected--label">
            <span class="">问题户:</span>
            <span class="">家庭困难、</span>
            <span class="">住房困难、</span>
            <span class="">家庭纠纷</span>
            <svg class="sign-selected--label-delete icon" aria-hidden="true">
                <use xlink:href="#icon-dustbin_icon"></use>
            </svg>
        </div>
        <!--单户操作时循环生成已选标签-->
    </div>
    <div class="sign-add">
        <div class="sign-add--title">
            <div class="sign-private--title">个人标签<span class="sign-add-btn">【新建】</span></div>
            <div class="sign-system--title">系统标签<span class="sign-add-btn">【新建】</span></div>
        </div>
        <div class="sign-add--content">
            <div class="sign-private--content">
                <!--单户操作时循环生成个人标签-->
                <div class="sign-private--label">
                    <div class="sign--label-key">资料核实</div>
                    <ul class="sign--label-value">
                        <li>已核实</li>
                        <li class="active">未核实</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit js-editor-label">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value to-up">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value to-up">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value to-up">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
                <div class="sign-private--label">
                    <div class="sign--label-key">问题户</div>
                    <ul class="sign--label-value to-up">
                        <li>家庭困难</li>
                        <li>家庭纠纷</li>
                        <li>住房困难</li>
                        <li class="sign--label-edit">编辑标签</li>
                    </ul>
                </div>
            </div>
            <div class="sign-system--content">
                <!--单户操作时循环生成系统标签-->
                <div class="sign-system--label">
                    1
                </div>
                <div class="sign-system--label">
                    2
                </div>
            </div>
        </div>
    </div>
    <div class="sign-dialog-box">
        <div class="sign-dialog">

        </div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph016/js/batch_mark.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph016/iconfont/iconfont.js"/>
<script>
    $(document).ready(function () {
        console.log("初始化")
        <%--ph016.init('${taskId}');--%>
        $(".js-editor-label").on("click", function () {
            console.log("编辑")
        })
    });
</script>
