package org.rril.bungeelogin.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.rril.bungeelogin.bungeelogin;
import org.rril.bungeelogin.MD5;

/**
 * Command manager for bungeelogin plugin
 *
 * @author Stakzz
 * @version 0.9.0
 */
public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {

        // ADMIN COMMANDS
        if (cmd.getName().equalsIgnoreCase("bungeelogin")) {
            if (sender.hasPermission("bungeelogin.admin")) {
                if (args.length == 0) {
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "This command require more arguments");
                    return false;
                }

                // ADMIN REGISTER COMMAND
                if (args[0].equalsIgnoreCase("register")) {
                    if (args.length == 3) {
                        if (bungeelogin.isRegistered(Bukkit.getPlayer(args[1]).getUniqueId().toString())) {
                            sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + args[1] + " is already registered");
                            return true;
                        }
                        if (bungeelogin.vAuth) {
                            try {
                                bungeelogin.vAuthDatabaseConnection.register(Bukkit.getPlayer(args[1].toString()), args[2].toString(), args[2].toString());
                            } catch (Exception e)  {
                                bungeelogin.logger.log(Level.SEVERE, "vAuth Exception - " + e.toString());
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                                return true;
                            }
                        } else {
                            String query = "INSERT INTO users(username, password) VALUES('" + Bukkit.getPlayer(args[1]).getUniqueId().toString() + "','" + MD5.crypt(args[2]) + "');";
                            try {
                                bungeelogin.databaseConnection.executeUpdate(query);
                            } catch (SQLException e) {
                                bungeelogin.logger.log(Level.SEVERE, "SQL Exception - " + e.toString());
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register " + args[1]);
                                return true;
                            }
                        }
                        sender.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + args[1] + " is successfully registered");
                        return true;
                    }
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Syntax error.");
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.GOLD + "/bungeelogin register <player> <password>");
                    return true;
                }

                // ADMIN UNREGISTER COMMAND
                if (args[0].equalsIgnoreCase("unregister")) {
                    if (args.length == 2) {
                        if (!bungeelogin.isRegistered(args[1])) {
                            sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + args[1] + " is not registered");
                            return true;
                        }
                        if (bungeelogin.vAuth) {
                            try {
                                bungeelogin.vAuthDatabaseConnection.remove(Bukkit.getPlayer(args[1].toString()));
                            } catch (Exception e)  {
                                bungeelogin.logger.log(Level.SEVERE, "vAuth Exception - " + e.toString());
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                                return true;
                            }
                        } else {
                            String query = "DELETE FROM users WHERE username = '" + Bukkit.getPlayer(args[1]).getUniqueId().toString() + "';";
                            try {
                                bungeelogin.databaseConnection.executeUpdate(query);
                            } catch (SQLException e) {
                                bungeelogin.logger.log(Level.SEVERE, "SQL Exception - " + e.toString());
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to unregister " + args[1]);
                                return true;
                            }
                        }
                        sender.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + args[1] + " is successfully unregistered");
                        return true;
                    }
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Syntax error.");
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.GOLD + "/bungeelogin unregister <player>");
                    return true;
                }

                // ADMIN CHANGEPW COMMAND
                if (args[0].equalsIgnoreCase("changepw")) {
                    if (args.length == 3) {
                        if (!bungeelogin.isRegistered(args[1])) {
                            sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + args[1] + " is not registered");
                            return true;
                        }
                        if (bungeelogin.vAuth) {
                            try {
                                bungeelogin.vAuthDatabaseConnection.register(Bukkit.getPlayer(args[2].toString()), args[3].toString(), args[3].toString());
                            } catch (Exception e)  {
                                bungeelogin.logger.log(Level.SEVERE, "vAuth Exception - " + e.toString());
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                                return true;
                            }
                        } else {
                            String query = "UPDATE users SET password = '" + MD5.crypt(args[2]) + "' WHERE username = '" + Bukkit.getPlayer(args[1]).getUniqueId().toString() + "';";
                            try {
                                bungeelogin.databaseConnection.executeUpdate(query);
                            } catch (SQLException e) {
                                bungeelogin.logger.log(Level.SEVERE, "SQL Exception - " + e.toString());
                                sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to change " + args[1]
                                                                            + "'s password.");
                                return true;
                            }
                        }
                        sender.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "You are successfully change " + args[1] + "'s password");
                        return true;
                    }
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Syntax error.");
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.GOLD + "/bungeelogin changepw <player> <newpassword>");
                    return true;
                }

                // ADMIN RELOAD COMMAND
                if (args[0].equalsIgnoreCase("reload")) {
                    bungeelogin.pluginManager.disablePlugin(bungeelogin.plugin);
                    bungeelogin.pluginManager.enablePlugin(bungeelogin.plugin);
                    bungeelogin.plugin.getServer().broadcastMessage(bungeelogin.PROMPT + ChatColor.GOLD + "bungeelogin has been reloaded, please log back in");
                    bungeelogin.plugin.getServer().broadcastMessage(bungeelogin.PROMPT + ChatColor.GOLD + "/login <password>");
                    sender.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "Plugin reloaded!");
                    return true;
                }
                return true;
            }
            return true;
        }

        // REGISTER COMMAND
        if ((cmd.getName().equalsIgnoreCase("register")) && (sender instanceof Player)) {
            if (sender.hasPermission("bungeelogin.register")) {
                Player player = (Player) sender;

                if (bungeelogin.isRegistered(player)) {
                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "You are already registered");
                    return true;
                }

                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase(args[1])) {
                        if (bungeelogin.vAuth) {
                            try {
                                bungeelogin.vAuthDatabaseConnection.register(player, args[0].toString(), args[1].toString());
                            } catch (Exception e)  {
                                bungeelogin.logger.log(Level.SEVERE, "vAuth Exception - " + e.toString());
                                player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                                player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                                return true;
                            }
                        } else {
                            String query = "INSERT INTO users(username, password) VALUES('" + player.getUniqueId().toString() + "','" + MD5.crypt(args[0]) + "');";
                            try {
                                bungeelogin.databaseConnection.executeUpdate(query);
                            } catch (SQLException e) {
                                bungeelogin.logger.log(Level.SEVERE, "SQL Exception - " + e.toString());
                                player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                                player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                                return true;
                            }
                        }
                        bungeelogin.sessions.put(player.getUniqueId().toString(), player.getAddress().getHostString());
                        player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "You are successfully registered");
                        player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "Have fun !");
                        return true;
                    }
                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Passwords are not equal");
                    return true;
                }
                player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Syntax error");
                return false;
            }
            return true;
        }

        // LOGIN COMMAND
        if ((cmd.getName().equalsIgnoreCase("login")) && (sender instanceof Player)) {
            if (sender.hasPermission("bungeelogin.login")) {
                Player player = (Player) sender;

                if (bungeelogin.isLogged(player)) {
                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "You are already logged in");
                    return true;
                }
                if (args.length == 1) {
                    boolean passwdCorrect = false;
                    if (bungeelogin.vAuth) {
                        try {
                            if(bungeelogin.vAuthDatabaseConnection.loginCheck(player, args[0]) == true){
                                passwdCorrect = true;
                            }
                        } catch (Exception e) {
                            bungeelogin.logger.log(Level.SEVERE, "vAuth Exception - " + e.toString());
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                            return true;
                        }
                    } else {
                        String query = "SELECT `block` FROM `users` WHERE `username` = '" + player.getUniqueId().toString() + "' AND `password` = '" + MD5.crypt(args[0]).toString() + "';";
                        try {
                            ResultSet result = bungeelogin.databaseConnection.executeQuery(query);
                            result.first();

                            if (result.getString("block").equalsIgnoreCase("0")) {
                                passwdCorrect = true;
                            }
                        } catch (SQLException e) {
                            bungeelogin.logger.log(Level.SEVERE, "SQL Exception - " + e.toString());
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to login.");
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                            return true;
                        }
                    }

                    if (passwdCorrect) {
                        bungeelogin.sessions.put(player.getUniqueId().toString(), player.getAddress().getHostString());
                        player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "You are successfully logged in.");
                        player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "Have fun !");
                    } else {
                        player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Incorrect password. Try again.");
                    }
                    return true;
                }
                player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Syntax error");
                return false;
            }
            return true;
        }

        // LOGOUT COMMAND
        if ((cmd.getName().equalsIgnoreCase("logout")) && (sender instanceof Player)) {
            if (sender.hasPermission("bungeelogin.logout")) {
                Player player = (Player) sender;

                if (!bungeelogin.isLogged(player)) {
                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "You are not logged in");
                    return true;
                }

                bungeelogin.sessions.remove(player.getUniqueId().toString());
                player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "You are successfully logged out.");
                player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "See you.");
                return true;
            }
            return true;
        }

        // CHANGEPW COMMAND
        if ((cmd.getName().equalsIgnoreCase("changepw")) && (sender instanceof Player)) {
            if (sender.hasPermission("bungeelogin.changepw")) {
                Player player = (Player) sender;

                if (!bungeelogin.isLogged(player)) {
                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "You aren't logged in");
                    return true;
                }
                if (args.length == 3) {
                    boolean passwdCorrect = false;
                    if (bungeelogin.vAuth) {
                        try {
                            passwdCorrect = bungeelogin.vAuthDatabaseConnection.loginCheck(player, args[0]);
                        } catch (Exception e) {
                            bungeelogin.logger.log(Level.SEVERE, "vAuth Exception - " + e.toString());
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                            return true;
                        }
                    } else {
                        String query = "SELECT block FROM users WHERE username = '" + player.getUniqueId().toString() + "' AND `password` = " + MD5.crypt(args[0]).toString() + ";";
                        try {
                            ResultSet result = bungeelogin.databaseConnection.executeQuery(query);
                            result.first();

                            if (result.getString("block").equalsIgnoreCase("0")) {
                                passwdCorrect = true;
                            }
                        } catch (SQLException e) {
                            bungeelogin.logger.log(Level.SEVERE, "SQL Exception - " + e.toString());
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to change your password.");
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                            return true;
                        }
                    }
                    if (passwdCorrect) {
                        if (args[1].equalsIgnoreCase(args[2])) {
                            if (bungeelogin.vAuth) {
                                try {
                                    bungeelogin.vAuthDatabaseConnection.register(player, args[1], args[2]);
                                } catch (Exception e) {
                                    bungeelogin.logger.log(Level.SEVERE, "vAuth Exception - " + e.toString());
                                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to register.");
                                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                                    return true;
                                }
                            } else {
                                String query1 = "UPDATE users SET password = '" + MD5.crypt(args[1]) + "' WHERE username = '" + player.getUniqueId().toString() + "';";
                                try {
                                    bungeelogin.databaseConnection.executeUpdate(query1);
                                } catch (SQLException e) {
                                    bungeelogin.logger.log(Level.SEVERE, "SQL Exception - " + e.toString());
                                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "An error occured when you try to change your password.");
                                    player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Try again or contact an admin");
                                    return true;
                                }
                            }
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "You are successfully change your password");
                            player.sendMessage(bungeelogin.PROMPT + ChatColor.GREEN + "Have fun !");
                            return true;
                        }
                        player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "New passwords are not equal");
                        return true;
                    } else {
                        player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Incorrect password. Try again.");
                    }
                    return true;
                }
                player.sendMessage(bungeelogin.PROMPT + ChatColor.RED + "Syntax error");
                return false;
            }
            return true;
        }
        return false;
    }

}
