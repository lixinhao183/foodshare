package xinhao.foodshare.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xinhao.foodshare.mapper.MenuMapper;
import xinhao.foodshare.mapper.RoleMenuMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.UserRoleMapper;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.entity.permission.Menu;
import xinhao.foodshare.pojo.entity.permission.RoleMenu;
import xinhao.foodshare.pojo.entity.permission.UserRole;
import xinhao.foodshare.pojo.vo.LoginUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);
        //如果查询不到数据就通过抛出异常来给出提示
        if(Objects.isNull(user)){
            throw new RuntimeException("用户名或密码错误");
        }

        //根据用户查询权限信息 添加到LoginUser中
        List<String> list = new ArrayList<>();
        // 1. 查询用户角色
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, user.getUserId());
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
            
            // 2. 查询角色菜单关联
            LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.in(RoleMenu::getRoleId, roleIds);
            List<RoleMenu> roleMenus = roleMenuMapper.selectList(roleMenuWrapper);
            
            if (!roleMenus.isEmpty()) {
                List<Long> menuIds = roleMenus.stream()
                        .map(RoleMenu::getMenuId)
                        .collect(Collectors.toList());
                
                // 3. 查询菜单权限标识
                if (!menuIds.isEmpty()) {
                    LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
                    menuWrapper.in(Menu::getMenuId, menuIds)
                               .eq(Menu::getStatus, "0"); // 假设状态0表示正常
                    List<Menu> menus = menuMapper.selectList(menuWrapper);
                    
                    // 4. 收集权限标识
                    list = menus.stream()
                            .map(Menu::getPermKey)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                }
            }
        }

        //封装成UserDetails对象返回
        return new LoginUser(user,list);
    }
}

