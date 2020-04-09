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
            , $ = layui.$;

        function layerAnim6(msg) {
            layer.msg(msg, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        }

        /* 监听表单提交 */
        $("input[name='submitInsertClass']").on("click", function () {
            let CLASS_NAME = $("input[name='className']")
                , CLASS_NAME_VALUE = CLASS_NAME.val();

            if(utils.isEmpty(CLASS_NAME_VALUE)){
                layerAnim6('请输入班级名称！');
                CLASS_NAME.focus();
                return false;
            }

            let teacherId = sessionStorage.getItem("roleId")
                , courseId = sessionStorage.getItem("selectedCourse");
            if(utils.isEmpty(teacherId)){
                layerAnim6('教师信息为空，新建班级出现错误！');
                return false;
            }
            if(utils.isEmpty(courseId)){
                layerAnim6('课程信息为空，新建班级出现错误！');
                return false;
            }

            $.ajax({
                url: utils.getDomainName() + '/stuClass/insertClass',
                type: 'post',
                dataType: 'json',
                timeout: '10000',
                data: {
                    className: CLASS_NAME_VALUE,
                    courseId: courseId,
                    teacherId: teacherId
                },
                success: function (data) {
                    let message = data.msg;
                    if (data.success) {
                        let map = data.map;
                        layer.msg('添加成功！', {anim: 0, time: 1000}, function () {
                            sessionStorage.setItem("selectedClass", map.classId);
                            sessionStorage.setItem("selectedClassName", map.className);
                            //弹窗结束后
                            window.parent.location.reload();//修改成功后刷新父界面
                        });
                    } else {
                        if (typeof message === undefined || utils.isEmpty(message))
                            layer.msg("添加失败！", {time: 2000, anim: 0});
                        else
                            layer.msg(message, {time: 2000, anim: 0});
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        });
        /*-----------END 监听表单提交------------*/
    });//end layui.use
});