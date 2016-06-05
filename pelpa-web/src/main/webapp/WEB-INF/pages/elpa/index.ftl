<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Access-Control-Allow-Origin" content="*"/>
    <meta http-equiv="X-UA-Compatible " content="IE=edge,chrome=1" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <link rel='shortcut icon' type='image/x-icon' href='/assets/images/favicon.ico' />
    <title>pelpa</title>

    <link href="/assets/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="/assets/css/semantic.min.css">
    <link rel="stylesheet" type="text/css" href="/assets/css/googlecode.min.css">

    <link rel="stylesheet" href="/assets/css/pelpa.css" type="text/css" />
    <link rel="stylesheet" href="/assets/css/jquery.dataTables.min.css" type="text/css" />
    <link rel="stylesheet" href="/assets/css/dataTables.semanticui.min.css" type="text/css" />
</head>
<body>

<#include "/layout/navbar.ftl"/>

<div class="ui container ak-main-container">
    <section class=" page-header">
        <h1>
            <span>POPKIT ELPA</span>
            <small> (Popkit’s Emacs Lisp Package Archive)</small>
        </h1>
    </section>

    <div class="row">
        <div class="col-md-8">
            <section class="jumbotron ui segment">popkit elpa是elpa的国内镜像，满足国内emacs用户快速安装包的需求:
                <ul>
                    <li>该项目运行在作者本人国内的VPS(服务器地址：深圳)上；</li>
                    <li>每天会循环更新<a href="https://github.com/aborn/popkit-elpa/tree/master/recipes">recipes</a>列表里的包(从recipe指定的源进行下载)；</li>
                    <li>每次更新完成后，休息2小时后会进行下一次build；</li><li>popkit elpa里的recipes，每2小时保持与melpa里的recipes同步；</li>
                    <li>你可以通过向<a href="https://github.com/aborn/popkit-elpa">pokit-elpa</a>提交pull request添加自己的包;</li>
                    <li>如有任何问题，请联系aborn(<a href="https://github.com/aborn" target="_blank">https://github.com/aborn</a>)</li>
                    <li><strong>支持该项目</strong>，支付宝扫一扫右边的二维进行捐赠。</li>
                </ul>
            </section>
        </div>
        <div class="col-md-4">
            <div class="alert alert-warning"><strong>当前构建开始于:</strong><span>3 minutes ago</span><span>, 上次耗时 unknown</span></div>
            <div><img src="/assets/images/donate.png" height="220px" width="220px"></div>
        </div>
    </div>

    <section id="packages">
        <h2 style="margin-top: 25px">当前库中含有3,087 个包
            <small>8,509 个下载量 磁盘使用：30.80% 磁盘剩余容量：27.2 GiB</small>
        </h2>
        <p>
            <input type="search" placeholder="Enter filter terms" autofocus="" class="form-control">
            <span class="help-block">3087 matching package(s)</span>
        </p>
        <table id="package-list" class="ui green table striped selectable">
            <thead>
            <tr>
                <th>Package</th>
                <th>Description</th>
                <th>Version</th>
                <th>Recipe</th>
                <th>Source</th>
                <th>DLs</th>
            </tr>
            </thead>
            <tbody>
            <tr>

                <td><a href="/#/0blayout">0blayout</a></td>
                <td><a href="/#/0blayout">Layout grouping with ease</a></td>
                <td class="version"><a href="packages/0blayout-20160515.1913.el">20160515.1913 <span class="glyphicon glyphicon-download"></span></a></td>
                <td class="recipe"><a href="https://github.com/milkypostman/melpa/blob/master/recipes/0blayout"><span class="glyphicon glyphicon-cutlery"></span>Githug</a></td>
                <td class="source"><a href="https://github.com/etu/0blayout-mode">github</a></td>
                <td>6</td></tr>
            </tr>

            <tr>

                <td><a href="/#/0blayout">0blayout</a></td>
                <td><a href="/#/0blayout">Layout grouping with ease</a></td>
                <td class="version"><a href="packages/0blayout-20160515.1913.el">20160515.1913 <span class="glyphicon glyphicon-download"></span></a></td>
                <td class="recipe"><a href="https://github.com/milkypostman/melpa/blob/master/recipes/0blayout"><span class="glyphicon glyphicon-cutlery"></span>Githug</a></td>
                <td class="source"><a href="https://github.com/etu/0blayout-mode">looo</a></td>
                <td>6</td></tr>
            </tr>

            </tbody>
        </table>
    </section>
</div>
<script src="/assets/js/jquery.js"></script>
<script src="/assets/js/jquery.dataTables.min.js"></script>
<script src="/assets/js/dataTables.semanticui.min.js"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $('#package-list').dataTable();
    });

</script>
</body>
</html>
