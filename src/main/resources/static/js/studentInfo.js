require.config({
    baseUrl: 'js',
    paths: {
        utils: 'utils/utils',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            exports: 'layui'
        }
    }
});

require(['layui', 'utils'], function (layui, utils) {
    layui.use(['form', 'layer'], function () {
        let layer = layui.layer
            , form = layui.form
            , $ = layui.$;

        function layerAnim6(msg) {
            layer.msg(msg, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        }

        let stuId = sessionStorage.getItem("roleId");
        if(utils.isEmpty(stuId)){
            layerAnim6('学生信息为空，操作出现错误！');
            return false;
        }

        //自定义验证规则
        form.verify({
            title: function(value){
                if(value.length < 5){
                    return '标题至少得5个字符啊';
                }
            }
            ,password: [
                /^[\S]{6,12}$/
                ,'密码必须6到12位，且不能出现空格'
            ]
        });

        //监听提交
        form.on('submit(studentInfoSubmit)', function(data){
            layer.alert(JSON.stringify(data.field), {
                title: '最终的提交信息'
            });
            return false;
        });

        //表单赋值
        $(function () {
            $.ajax({
                url: utils.getDomainName() + "/student/getStudentInfo",
                data: {
                    stuId: stuId
                },
                dataType: 'json',// 服务器返回json格式数据
                type: 'get',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        form.val('studentInfo', json['obj']);
                    } else {
                        let message = json['msg'];
                        if (typeof message !== undefined && !utils.isEmpty(message) && message !== "")
                            layer.msg(message);
                        else
                            layer.msg('获取学生信息失败！');
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                    layer.msg('服务器出错，获取学生信息失败！');
                }
            });//end ajax
        });

    });//end layui.use
});