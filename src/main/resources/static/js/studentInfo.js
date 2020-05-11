require.config({
    baseUrl: 'js',
    paths: {
        utils: 'utils/utils',
        encrypt: 'utils/encrypt',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            exports: 'layui'
        }
    }
});

require(['layui', 'utils', 'encrypt'], function (layui, utils, encrypt) {
    layui.use(['form', 'layer', 'upload'], function () {
        let layer = layui.layer
            , form = layui.form
            , upload = layui.upload
            , $ = layui.$;

        let stuId = sessionStorage.getItem("roleId");
        if (utils.isEmpty(stuId)) {
            utils.layerAnim6('学生信息为空，操作出现异常！', 1000, layer);
            return false;
        }

        let initFromData;

        //监听信息提交
        form.on('submit(studentInfoSubmit)', function (data) {
            if (utils.verifyLayerWhetherNull("phone",
                /^1[3-9]\d{9}$/,
                '格式错误，电话号码支持13-19开头，且为11位',
                $, layer)) return false;
            if (utils.verifyLayerWhetherNull("qq",
                /^\d{6,11}$/,
                '格式错误，QQ号码应为6~11位',
                $, layer)) return false;
            if (utils.verifyLayer("name",
                /^[\u4e00-\u9fa5]{2,13}$/,
                '格式错误，暂只支持中文，姓名应为2~13个字符',
                $, layer)) return false;
            if (utils.verifyLayer("stuNumber",
                /^\w{4,12}$/,
                '格式错误，学号应为4~11位',
                $, layer)) return false;
            if (utils.verifyLayerWhetherNull("school",
                /^[\u4e00-\u9fa5\w]{1,55}$/,
                '格式错误，学校名称不超过55个字符',
                $, layer)) return false;
            if (utils.verifyLayerWhetherNull("department",
                /^[\u4e00-\u9fa5\w]{1,20}$/,
                '格式错误，院系名称不超过20个字符',
                $, layer)) return false;
            if (utils.verifyLayerWhetherNull("major",
                /^[\u4e00-\u9fa5\w]{1,20}$/,
                '格式错误，专业不超过20个字符',
                $, layer)) return false;
            if (utils.verifyLayerWhetherNull("enrollmentYear",
                /^(20)\d{2}$/,
                '格式错误，入学年份应以“20”开头，且为4位数字',
                $, layer)) return false;

            //判断数据是否修改
            let formData = $("#studentInfoForm").serializeArray();
            if(JSON.stringify(initFromData) === JSON.stringify(formData)){
                layer.msg("无数据修改", {time: 2000});
                return false;
            }

            $.ajax({
                url: utils.getDomainName() + "/student/updateStudentInfo/",
                data: {
                    id: data.field['id'],
                    email: data.field['email'],
                    phone: data.field['phone'],
                    qq: data.field['qq'],
                    name: data.field['name'],
                    stuNumber: data.field['stuNumber'],
                    school: data.field['school'],
                    department: data.field['department'],
                    major: data.field['major'],
                    enrollmentYear: data.field['enrollmentYear']
                },
                dataType: 'json',// 服务器返回json格式数据
                type: 'put',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        layer.msg("修改成功！", {time: 2000}, function () {
                            window.parent.location.reload();//修改成功后刷新父界面
                        });
                    } else {
                        let message = json['msg'];
                        if (typeof message !== undefined && !utils.isEmpty(message) && message !== "") {
                            layer.msg(message, {time: 2000}, function () {
                                window.location.reload();
                            });
                        } else {
                            layer.msg('修改学生信息失败！', {time: 2000}, function () {
                                window.location.reload();
                            });
                        }
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                    layer.msg('服务器出错，修改学生信息失败！');
                }
            });//end ajax
            return false;//阻止表单跳转
        });

        //监听密码修改
        form.on('submit(passwordSubmit)', function (data) {
            if (utils.verifyLayer("oldPassword",
                /^[\S]{6,12}$/,
                '无效密码',
                $, layer)) return false;
            if (utils.verifyLayer("newPassword",
                /^[\S]{6,12}$/,
                '密码必须6到12位，且不能出现空格',
                $, layer)) return false;
            //判断输入的新旧密码是否一样
            let oldPasswordDOM = $("input[name='oldPassword']");
            let newPasswordDOM = $("input[name='newPassword']");
            if (oldPasswordDOM.val() === newPasswordDOM.val()) {
                layer.msg("输入的两个密码一样，没必要修改", {anim: 5, time: 1000}, function () {
                    oldPasswordDOM.val("");
                    newPasswordDOM.val("");
                    oldPasswordDOM.focus();
                });
                return false;
            }

            $.ajax({
                url: utils.getDomainName() + "/updatePassword/" + stuId,
                data: {
                    oldPassword: encrypt.encryptWithHashing(data.field['oldPassword'], "MD5"),
                    newPassword: encrypt.encryptWithHashing(data.field['newPassword'], "MD5")
                },
                dataType: 'json',// 服务器返回json格式数据
                type: 'put',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        layer.msg("密码修改成功,下次登录生效！", {time: 2000}, function () {
                            window.parent.location.reload();//修改成功后刷新父界面
                        });
                    } else {
                        let message = json['msg'];
                        if (typeof message !== undefined && !utils.isEmpty(message) && message !== "") {
                            layer.msg(message, {time: 2000}, function () {
                                window.location.reload();
                            });
                        } else {
                            layer.msg('密码修改失败！', {time: 2000}, function () {
                                window.location.reload();
                            });
                        }
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                    layer.msg('服务器出错，密码修改失败！');
                }
            });//end ajax
            return false;//阻止表单跳转
        });

        //图片上传
        let uploadInst = upload.render({
            elem: '#uploadImage'
            ,url: utils.getDomainName() + '/uploadFile/uploadImage/' + stuId //改成您自己的上传接口
            ,before: function(obj){
                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#uploadImageView').attr('src', result); //图片链接（base64）
                });
            }
            ,done: function(res){
                //如果上传失败
                if(res.code > 0){
                    return layer.msg('上传失败');
                }
                //上传成功
                layer.msg("上传图片成功！", {icon:6, time: 2000}, function () {
                    window.parent.location.reload();
                });
            }
            ,error: function(){
                //演示失败状态，并实现重传
                let demoText = $('#uploadImageText');
                demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
                demoText.find('.demo-reload').on('click', function(){
                    uploadInst.upload();
                });
            }
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
                        let obj = json['obj'];
                        $.each(obj, function (i, o) {
                            if (o === null || o === "") {
                                $("input[name=" + i + "]").prop("placeholder", "空");
                            }
                        });
                        form.val('studentInfo', obj);
                        initFromData = $("#studentInfoForm").serializeArray();
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