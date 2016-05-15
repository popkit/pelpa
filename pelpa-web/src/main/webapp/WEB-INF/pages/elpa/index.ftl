<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Access-Control-Allow-Origin" content="*"/>
    <meta http-equiv="X-UA-Compatible " content="IE=edge,chrome=1" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <link rel='shortcut icon' type='image/x-icon' href='/assets/image/favicon.ico' />
    <title>pelpa</title>
<#include "/common/semantic.ftl">
</head>
<body>

<#include "/layout/navbar.ftl"/>

<div class="ui container ak-main-container">
    <div class="ui green message">
            <div>${pkgReady}</div>
            <div>${pkgOnging}</div>
            <div>${pkgFinished}</div>
            <div>${percent}</div>
    </div>

<#if (unstarted?? && unstarted?size > 0)>
    <table class="ui red table">
        <thead>
        <tr>
            <th>包名</th>
            <th>下载状态</th>
            <th>构建状态</th>
            <th>开始时间</th>
            <th>结束时间</th>
        </tr>
        </thead>
        <tbody>
            <#list unstarted as item >
            <tr>
                <td>${item.pkgName}</td>
                <td>${item.fetchStatus}</td>
                <td>${item.buildStatus}</td>
                <td>${(item.startTime?string('yy-MM-dd hh:mm'))!}</td>
                <td>${(item.endTime?string('yy-MM-dd hh:mm'))!}</td>
            </tr>
            </#list>
        </tbody>
    </table>
</#if>


<#if (onging?? && onging?size > 0)>
    <table class="ui orange table">
        <thead>
        <tr>
            <th>包名</th>
            <th>下载状态</th>
            <th>构建状态</th>
            <th>开始时间</th>
            <th>结束时间</th>
        </tr>
        </thead>
        <tbody>
            <#list onging as item >
            <tr>
                <td>${item.pkgName}</td>
                <td>${item.fetchStatus}</td>
                <td>${item.buildStatus}</td>
                <td>${(item.startTime?string('yy-MM-dd hh:mm'))!}</td>
                <td>${(item.endTime?string('yy-MM-dd hh:mm'))!}</td>
            </tr>
            </#list>
        </tbody>
    </table>
</#if>

<#if (finished?? && finished?size > 0)>
    <table class="ui green table">
        <thead>
        <tr>
            <th>包名</th>
            <th>下载状态</th>
            <th>构建状态</th>
            <th>开始时间</th>
            <th>结束时间</th>
        </tr>
        </thead>
        <tbody>
            <#list finished as item >
            <tr>
                <td>${item.pkgName}</td>
                <td>${item.fetchStatus}</td>
                <td>${item.buildStatus}</td>
                <td>${(item.startTime?string('yy-MM-dd hh:mm'))!}</td>
                <td>${(item.endTime?string('yy-MM-dd hh:mm'))!}</td>
            </tr>
            </#list>
        </tbody>
    </table>
</#if>
</div>

</body>
</html>
