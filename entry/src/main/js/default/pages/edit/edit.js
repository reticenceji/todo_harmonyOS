import router from '@system.router';

export default {
    data:{
        title: "",
        text: "",
        content: {},
    },
    onShow(){
        console.info(this.content.toString());
        if (this.content == null) {
            this.title = "";
            this.text = "";
        }
        else {
            this.title = this.content.title;
            this.text = this.content.text;
        };
        console.info("onShow()");
    },
    ClickToBack() {
        router.back();
        console.info("ClickToBack()");
    },
    ClickToSave(){
        // 需要和数据库交互
    },
}