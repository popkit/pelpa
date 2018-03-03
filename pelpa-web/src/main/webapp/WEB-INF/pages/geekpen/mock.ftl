<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Access-Control-Allow-Origin" content="*"/>
    <meta http-equiv="X-UA-Compatible " content="IE=edge,chrome=1" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <link rel='shortcut icon' type='image/x-icon' href='/assets/images/favicon.ico' />
    <title>geekpen</title>
<#include "/common/semantic.ftl">
</head>
<body>

<#include "/layout/navbar.ftl"/>

<div class="ui container ak-main-container">
    <button class="ui green button" id="mockButton">mock</button>
</div>

<script type="text/javascript">
    $('#mockButton').on('click', function (e) {
        console.log("click");
        var postData = {};
        var book = {};
        book.bookName = "这个历史挺靠谱";
        book.progress = 73;
        book.type = 2;
        book.time = new Date('2018-03-04 17:55:55');

        postData.openid = 'o6Jzu0OvdlwmcmQ2N1FtFpIfslx4';
        postData.records = [];
        postData.records.push(book);

        var user = {};
        user.openid = 'o6Jzu0OvdlwmcmQ2N1FtFpIfslx4';
        user.userName = '大宝大';
        user.avatar = 'https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLvgf4G3t9GVibcAW4QAbCGNXK4JkEm2XibUQLkLBrVPBKCkeqk0BL0bg5BGRVlHwQ4YKBF1bfleXHQ/0';
        user.city = 'Changning';
        user.country = 'China';
        user.gender = '1';
        user.province = 'Shanghai';
        user.language = 'zh_CN';
        postData.user = user;

        $.ajax({
            type : 'post',
            url : '/geekpen/api/record.json',
            data: JSON.stringify(postData),
            dataType:'json',
            contentType: "application/json",
            success: function(ajaResult){
                if (ajaResult.success) {
                    console.log("操作成功")
                } else {
                    console.log(ajaResult.info);
                }
            },
            error : function(){

            }
        });
    })
</script>

</body>
</html>
