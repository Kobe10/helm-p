# 领导驾驶舱后台管理接口出参入参定义
## 图层维护
1. 新增修改图层
    * 接口 : /helm-admin/shfb/layer/define/save  post
    * 入参 : 
        ```
            [   //入参
                {
                   "prjCd": "143",
                   "mapLayerDefId":""//图层编号 新增为空 修改不为空
                   "layIconDocId": "", //图层图标Id
                   "showIconDocId": "", //显示图标Id
                   "mapLayerType": "",//图层类型 1、标记;2、多边形
                   "showStyle": "",//显示样式
                   "attrDef": ""//图层属性 数据定义中的图层属性
                },
                //出参
                {
                   "success": true,
                   "errMsg": "",
                   "data": ""
                }
            ]
        ```
2. 删除图层（支持批量）
    * 接口 : /helm-admin/shfb/layer/define/delete  post
    * 入参 : 
    ```
        [   //入参
            {
               "prjCd": "143",
               "mapLayerDefIds":""//(必须)图层编号  逗号隔开
            },
             //出参
            {
               "success": true,
               "errMsg": "",
               "data": ""
            }
        ]
    ```

## 数据维护
1. 新增修改数据
    * 接口 : helm-admin/shfb/layer/data/save  post
    * 入参 : 
        ```
            [   //入参
                {
                   "prjCd": "143",
                   "layperType":"1"//（必须）新增类型 1通用图层数据 2摄像头图层数据 345 后续可能会增加根据扩展图层需要
                   "data": { //属性编码：属性值 
                       //通用图层的数据 
                       "mapDataId":""//数据编号 新增为空 修改不为空
                       "mapDataName":""//数据名称
                       "mapDataAddr":""//数据地址
                       "mapLayerDefId": "", //（必须）图层编号
                       
                       //下面都是字段属性或者是扩展数据的字段属性
                       "camName":"",//元素名称
                       "camUser":"",
                       "camPwd":""
                   }
                },
                //出参
                {
                  "success": true,
                  "errMsg": "",
                  "data": {
                    "mapDataId": "2001",//图层数据的Id
                  }
                }
            ]
        ```
2. 删除数据（批量）
    * 接口 : helm-admin/shfb/layer/data/delete  post
    * 入参 : 
    ```
        [   //入参
            {
               "prjCd": "143",
               "mapDataIds":"",//(必须)数据编号  逗号隔开
               "layperType":"2"//(必须)类型 1通用图层数据 2摄像头图层数据 345 后续可能会增加根据扩展图层需要
            },
             //出参
            {
               "success": true,
               "errMsg": "",
               "data": ""
            }
        ]
    ```
3. 导入数据（模板）
    * 接口 : helm-admin/shfb/layer/data/import  post
    * 入参 : 
    ```
        [   //入参
            {
               "prjCd": "143",
               "importMbFile":"",//(必须)文件名称
            },
             //出参
            {
               "success": true,
               "errMsg": "",
               "data": ""
            }
        ]
    ```
    
4. 下载模板
    * 接口 : helm-admin/shfb/layer/data/export  get
    
    参数 ： templateType:模板类型(必须)
    

## 数据关联
1. 新增修改数据关联（批量）
    * 接口 : helm-admin/shfb/layer/bind/save  post
    * 入参 : 
    ```
        [   //入参
            {
               "prjCd": "143", 
               "mapDataIdA":""//(必须)数据编号A端
               "mapDataIdZ":""//数据编号Z端
               "mapLayerDefId":""//(必须)数据编号Z端所属图层Id
               "mapDataRelCd":""//关联类型  0包含
            },
             //出参
            {
               "success": true,
               "errMsg": "",
               "data": ""
            }
        ]
    ```
    
2. 数据关联查询
    * 接口 : helm-admin/shfb/layer/bind/query  post
    * 入参 : 
    ```
        [   //入参
            {
               "prjCd": "143", 
               "mapDataIdA":""//数据编号A端
               "mapLayerDefId":""//数据编号Z端所属图层Id
               "mapDataRelCd":""//关联类型  0包含
            },
             //出参
            {
              "success": true,
              "errMsg": "",
              "data": {
                "bindData": [
                  {
                    "mapDataId": "",
                    "mapDataName": ""
                  },
                  {
                      "mapDataId": "",
                      "mapDataName": ""
                  }
                ]
              }
            }
        ]
    ```
## 图层查询
1. 图层查询
    * 接口 : helm-admin/shfb/layer/define/query  get
    * 入参 : 使用场景 三种：1 初始化地图查询 2 图层快捷模糊查询 3单个图层数据的详细查询
    ```
        [   //入参
            {
               "prjCd": "143",
               "keyWord":"查询关键字",//用来模糊查询
               "type":"",//(必须)1,2  1表示初始化地图查询  2表示图层快捷查询（不能为空）
               "mapLayerDefId":"图层Id",//只传该参数表示根据Id查询当前图层数据 用于修改某一个图层数据的查询操作
            },
             //出参
            {
               {
                 "success": true,
                 "errMsg": "",
                 "data": {
                   "layerInfo": [
                     {
                       "showStyle": "",
                       "attrDef": "",
                       "layIconDocId": "1000",
                       "mapLayerDefId": "1",
                       "mapLayerType": "1",
                       "mapLayerDefName": "哈哈哈",
                       "showIconDocId": "1002",
                       "mapLayerDefCd": ""
                     },
                     {
                       "showStyle": "",
                       "attrDef": "",
                       "layIconDocId": "1000",
                       "mapLayerDefId": "2",
                       "mapLayerType": "哈哈哈",
                       "mapLayerDefName": "嘻嘻嘻",
                       "showIconDocId": "1002",
                       "mapLayerDefCd": ""
                     }
                   ]
                 }
               }
            }
        ]
    ```
    
## 数据查询
1. 动态数据查询
    * 接口 : helm-admin/shfb/layer/define/query  get
    * 入参 : 
        ```
            [   //入参
                {
                   "prjCd": "143",
                   "queryType":"",//(必须)查询类型 1代表单一数据的查询  2代表通用所有图层数据查询  3代表针对某一图层进行查询
                   "data":{
                        "layerType":"",//(必须)实体类型 1通用实体 2摄像头实体 3.4.5.6依次递增
                        "mapDataId":"",//图层数据Id   场景：在通过id查询数据详细信息
                        "layerId":"",//图层Id   场景：查询名称或者地址
                        
                        "keyWord":"",//查询关键字   场景：查询名称或者地址
                        "queryName":"",//查询字段   场景：查询名称或者地址中的一个(单一查询)
                   }
                },
                
                //出参格式一：
                //根据数据Id进行查询 查询数据详细信息 出参
                {
                  "success": false,
                  "errMsg": "",
                  "data": {
                    "mapDataInfo": [
                      {
                        "data2": "ceshi",
                        "data1": "测试导入"
                      }
                    ]
                  }
                }
                
                {
                  "success": false,
                  "errMsg": "",
                  "data": {
                    "mapDataInfo": [
                      {
                        "camPwd": "shfb_718",
                        "cptPicTimeExp": "20180907181232",
                        "camName": "199",
                        "camIpAddr": "192.168.1.199",
                        "camUser": "admin"
                      }
                    ]
                  }
                }
                
                //出参格式二：
                //代表模糊数据的查询     出参
                {
                  "success": false,
                  "errMsg": "",
                  "data": {
                    "mapDataInfo": [
                      {
                        "mapDataId": "1",    //图层数据Id  
                        "mapDataAddr": "",//图层数据地址
                        "mapDataName": "楼宇"//图层数据名称
                      }
                    ]
                  }
                }
                
                //出参格式三：
                //代表通用模糊查询  根据地址或者名称
                {
                  "success": false,
                  "errMsg": "",
                  "data": {
                    "mapDataInfo": [
                      {
                        "mapDataId": "1",    //图层数据Id  
                        "mapLayerDefId": "1",//所属图层编号
                        "mapLayerDefName": "哈哈哈",//所属图层名称
                        "mapDataAddr": "",//图层数据地址
                        "mapDataName": "楼宇"//图层数据名称
                      },
                      {
                        "mapDataId": "2",
                        "mapLayerDefId": "2",
                        "mapLayerDefName": "嘻嘻嘻",
                        "mapDataAddr": "",
                        "mapDataName": "地块"
                      },
                      {
                        "mapDataId": "2000",
                        "mapLayerDefId": "1",
                        "mapLayerDefName": "哈哈哈",
                        "mapDataAddr": "",
                        "mapDataName": "测试查询关联数据哈"
                      },
                      {
                        "mapDataId": "2001",
                        "mapLayerDefId": "",
                        "mapLayerDefName": "",
                        "mapDataAddr": "",
                        "mapDataName": "sdfsdf"
                      }
                    ]
                  }
                }
            ]
        ``` 