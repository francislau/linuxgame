import{u as g,g as b,r as a,o as h,c as y,a as t,w as n,F as x,S as w,G as E,H as V,x as k,f as I,E as i,l as S}from"./vendor.1bc7fbb0.js";import{_ as C}from"./plugin-vue_export-helper.21dcd24c.js";const L=l=>(E("data-v-c87164c4"),l=l(),V(),l),z=L(()=>S("div",{class:"title"},"\u300A\u6311\u6218Linux\u300B\u540E\u53F0\u7BA1\u7406\u7CFB\u7EDF",-1)),B=k("\u767B\u5F55"),K={setup(l){const p=g(),o=b({username:"",password:""}),m={background:{color:{value:"#000"}},fpsLimit:60,interactivity:{events:{onClick:{enable:!0,mode:"push"},onHover:{enable:!0,mode:"grab"},resize:!0},modes:{bubble:{distance:400,duration:2,opacity:.8,size:40},push:{quantity:4},repulse:{distance:200,duration:.4}}},particles:{color:{value:"#EEE"},links:{color:"#EEE",distance:150,enable:!0,opacity:.5,width:1},collisions:{enable:!1},move:{direction:"none",enable:!0,outMode:"bounce",random:!1,speed:2,straight:!1},number:{density:{enable:!0,area:800},value:40},opacity:{value:.3},shape:{type:"circle"},size:{random:!0,value:5}},detectRetina:!0},u=()=>{console.log(o.value),I.post("/api/user/login",o.value).then(e=>{console.log(e),e.data.result=="success"?(localStorage.setItem("id",e.data.data.id),localStorage.setItem("realname",e.data.data.realname),localStorage.setItem("token",e.data.data.token),i({message:"\u767B\u5F55\u6210\u529F",type:"success"}),p.push("/")):i(e.data.message)}).catch(function(e){console.log(e)})};return(e,s)=>{const _=a("Particles"),r=a("el-input"),d=a("el-form-item"),f=a("el-button"),v=a("el-form");return h(),y(x,null,[t(_,{id:"tsparticles",options:m}),t(v,{class:"login"},{default:n(()=>[z,t(d,{label:"\u7528\u6237\u540D"},{default:n(()=>[t(r,{modelValue:o.value.username,"onUpdate:modelValue":s[0]||(s[0]=c=>o.value.username=c),placeholder:"\u8BF7\u8F93\u5165\u7528\u6237\u540D",autocomplete:"off"},null,8,["modelValue"])]),_:1}),t(d,{label:"\u5BC6\u3000\u7801"},{default:n(()=>[t(r,{modelValue:o.value.password,"onUpdate:modelValue":s[1]||(s[1]=c=>o.value.password=c),placeholder:"\u8BF7\u8F93\u5165\u5BC6\u7801",type:"password",autocomplete:"off",onKeyup:w(u,["enter"])},null,8,["modelValue","onKeyup"])]),_:1}),t(f,{onClick:u},{default:n(()=>[B]),_:1})]),_:1})],64)}}};var F=C(K,[["__scopeId","data-v-c87164c4"]]);export{F as default};
