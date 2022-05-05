<template>
    <!--引入粒子特效-->
    
    <el-form>
      <el-form-item label="班级名称">
        <el-input ref="aaa" v-model="input1"></el-input>
      </el-form-item>
    </el-form>

</template>
 
 
<script setup>
import {ref } from "vue";
 const input = ref('')
const input1 = ref("666");
</script>