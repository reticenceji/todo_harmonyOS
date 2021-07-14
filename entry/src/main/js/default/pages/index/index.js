// @ts-nocheck
import router from '@system.router';

export default {
    data: {
        title: "",
        search: "",
        number: 0,              // TODO总数
        add_delete: "+",        // 添加按钮/删除按钮
        choose_delete: false,   // 当为true的时候，选择删除元素
        todoList:
            [
                {title:"玩",date:"2020",text:"WOW"},
                {title:"玩",date:"2020",text:"WOW"},
                {title:"玩",date:"2020",text:"WOW"},
                {title:"玩",date:"2020",text:"WOW"},
            ],
    },
    // 初始化
    onInit() {
        this.title = this.$t('strings.title');
        this.search = this.$t('strings.search');
        this.number = this.todoList.length.toString() + this.$t('strings.number');
        console.info("onInit");
        console.info(this.textList);
    },
    // 长按TODO text 可以选择删除
    LongPressToDelete() {
        this.choose_delete = true;
    },
    // 单击TODO text 可以编辑 页面跳转
    ClickToEdit(x) {
        router.push ({
            uri:'pages/edit/edit', // 指定要跳转的页面
            params: {index: x, content: this.todoList[x]}
        });
    },
    // 单击按钮 添加/删除
    addTODO() {
        if (this.choose_delete == false){
            router.push ({
                uri:'pages/edit/edit',      // 指定要跳转的页面 记得在config中注册路由
                params: {index:this.todoList.length, content: null}
            });
            console.info("addBackup()");
        }
        else {
            // 删除

        }
        this.choose_delete = false;
        this.add_delete = "+";
    },
    // 选择删除的TODO
    ClickToDelete(e) {
        console.info("ClickToDelete"+e.checked.toString());
        if (e.checked) {
            this.add_delete = "-";
        }
        else {
            this.add_delete = "+";
        }
    }
}
