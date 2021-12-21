##ioc 容器设计原理及高级特性

ioc spring容器的内核, 控制反转,依赖对象的获取被反转

通过注解,将bean实例化后放入 BeanFactory 的 ApplicationContext 中,再调用的时候,根据名称处理bean对象
