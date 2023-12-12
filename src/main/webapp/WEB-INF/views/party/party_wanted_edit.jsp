<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header_footer/header.jspf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게시글 작성</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/39.0.2/super-build/ckeditor.js"></script>
    <style>
        .content {
            width: 1200px;
            margin: 0 auto;
            margin-top: 70px;
            background: #F2F2F2;
            border-width: 3px 1px 3px 1px;
            border-color: #73351F;
            border-style: groove;
        }
        .content-header{
            height: 100px;
            width: 100%;
            background: #A69668;
            border-bottom: 2px solid #73351F;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        h3{

            font-size: 40px;
            font-weight: bold;
        }

        .ck-editor__editable {
            height: 400px;
        }



        #editor {
            width: 100%;
            height: 600px;

        }

        #botContainer {
            display: flex;
            justify-content: space-between;
            height: 38px;
            margin-top: 50px;
        }

        input[type='radio'] {
            display: none;
        }

        .container_bottom{
            display: flex;
            background: #A69668;
            height: 10px;
            margin-top: 10px;
        }

    </style>
    <script>
        $(function(){

            let editor;
            CKEDITOR.ClassicEditor.create(document.getElementById("editor"), {
                    toolbar: {
                        items: [
                            'bold', 'italic', '|',
                            'fontSize', 'fontColor', 'fontBackgroundColor', 'highlight', '|',
                            'alignment', '|',
                            'insertImage', 'mediaEmbed', '|',
                            'horizontalLine','|',
                        ],
                        shouldNotGroupWhenFull: true
                    },
                    list: {
                        properties: {
                            styles: true,
                            startIndex: true,
                            reversed: true
                        }
                    },
                    placeholder: '내용을 입력해주세요.',
                    fontFamily: {
                        options: [
                            'default',
                            'Arial, Helvetica, sans-serif',
                            'Courier New, Courier, monospace',
                            'Georgia, serif',
                            'Lucida Sans Unicode, Lucida Grande, sans-serif',
                            'Tahoma, Geneva, sans-serif',
                            'Times New Roman, Times, serif',
                            'Trebuchet MS, Helvetica, sans-serif',
                            'Verdana, Geneva, sans-serif'
                        ],
                        supportAllValues: true
                    },
                    fontSize: {
                        options: [10, 12, 14, 'default', 18, 20, 22],
                        supportAllValues: true
                    },
                    htmlSupport: {
                        allow: [
                            {
                                name: /.*/,
                                attributes: true,
                                classes: true,
                                styles: true
                            }
                        ]
                    },
                    link: {
                        decorators: {
                            addTargetToExternalLinks: true,
                            defaultProtocol: 'https://',
                            toggleDownloadable: {
                                mode: 'manual',
                                label: 'Downloadable',
                                attributes: {
                                    download: 'file'
                                }
                            }
                        }
                    },
                    removePlugins: [
                        'CKBox',
                        'CKFinder',
                        'EasyImage',
                        'RealTimeCollaborativeComments',
                        'RealTimeCollaborativeTrackChanges',
                        'RealTimeCollaborativeRevisionHistory',
                        'PresenceList',
                        'Comments',
                        'TrackChanges',
                        'TrackChangesData',
                        'RevisionHistory',
                        'Pagination',
                        'WProofreader',
                        'MathType',
                        'SlashCommand',
                        'Template',
                        'DocumentOutline',
                        'FormatPainter',
                        'TableOfContents',
                        'PasteFromOfficeEnhanced'
                    ]
                }
            ).then(neweditor => {
                editor=neweditor;
                editor.model.document.on('change:data',()=>{
                    document.querySelector('#wantedcontent').value=editor.getData();
                });
            })
                .catch(err => {
                    console.error(err.stack);
                });
            //

            $('#title').on('input blur', function() {
                var title = $(this).val();
                if(title.length > 30) {
                    $(this).val(title.substring(0, 30));
                }
                if(title.length==0){
                    $(this).addClass('is-invalid').removeClass('is-valid');
                }else{
                    $(this).addClass('is-valid').removeClass('is-invalid');
                }
            });

            $('form').on('submit', function(e) {
                var editorContent = editor.getData();
                if(!editorContent) {
                    e.preventDefault();
                    alert('글 내용을 입력해 주세요.');
                    return false;
                }
                if($('#title').hasClass('is-invalid')){
                    e.preventDefault();
                    alert("제목이 정규직에 어긋납니다.");
                    return false;
                }
            });
        });
    </script>
</head>
<body>
<div class="content">
    <div class="content-header">
        <h3>파티홍보 게시판 수정</h3>
    </div>
    <div style="background: #D9D9D9">
    <form method="post" action="${pageContext.servletContext.contextPath}/party/wanted/editOk" class="needs-validation writeform" style="padding: 20px 20px 10px 20px; border-bottom: 2px solid #73351F" novalidate>
        <input type="hidden" id="wantedcontent" name="wantedcontent" value="${wvo.wantedcontent}">
        <input type="hidden" name="wantedid" value="${wvo.wantedid}">
        <input type="text" style="width: 40%" class="form-control" name="wantedtitle" id="title" placeholder="제목을 입력해 주세요." required maxlength="30" value="${wvo.wantedtitle}">
        <div class="invalid-feedback">
            제목을 입력해 주세요. (30자 이내)
        </div>

        <div class="button-container">
                <label class="btn btn-dark">
                    <input type="radio" name="party_partyid" value="${wvo.party_partyid}" checked readonly>
                    <span>${wvo.partyname}</span>
                </label>
        </div>
    </div>
    <div style="padding: 10px">
        <div id="editor">${wvo.wantedcontent}</div>

        <div id="botContainer">
            <div style="width: 50%" class="botContainer2">
            </div>
            <input type="submit"  class="btn btn-primary submitbtn" value="글수정" />
        </div>
        </form>
    </div>
    <div class="container_bottom"></div>
</div>
</body>
</html>
<%@include file="../header_footer/footer.jspf" %>