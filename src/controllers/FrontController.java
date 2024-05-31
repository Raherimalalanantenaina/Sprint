package mg.itu.prom16.controllers;

import mg.itu.prom16.annotations.*;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

import javax.swing.Spring;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import map.Mapping;

public class FrontController extends HttpServlet {
    private List<String> controller = new ArrayList<>();
    private String controllerPackage;
    boolean checked = false;
    HashMap<String, Mapping> lien = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        controllerPackage = getInitParameter("controller-package");
        this.scan();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String[] requestUrlSplitted = request.getRequestURL().toString().split("/");
        String controllerSearched = requestUrlSplitted[requestUrlSplitted.length-1];
        
        response.setContentType("text/html");

        if (!lien.containsKey(controllerSearched)) {
            out.println("<p>"+"Methode non trouver."+"</p>");
        }
        else {
            try{

                Mapping mapping = lien.get(controllerSearched);
                Class<?> clazz=Class.forName(mapping.getClassName());
                Method method=clazz.getMethod(mapping.getMethodeName());
                Object object=clazz.getDeclaredConstructor().newInstance();
                Object returnValue=method.invoke(object);
                String stringValue=(String) returnValue;
                out.println("Methode trouvee dans " + stringValue);
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public void scan() {
        try {
            String classesPath = getServletContext().getRealPath("/WEB-INF/classes");
            String decodedPath = URLDecoder.decode(classesPath, "UTF-8");
            String packagePath = decodedPath + "\\" + controllerPackage.replace('.', '\\');
            File packageDirectory = new File(packagePath);
            if (packageDirectory.exists() && packageDirectory.isDirectory()) {
                File[] classFiles = packageDirectory.listFiles((dir, name) -> name.endsWith(".class"));
                if (classFiles != null) {
                    for (File classFile : classFiles) {
                        String className = controllerPackage + '.'
                                + classFile.getName().substring(0, classFile.getName().length() - 6);
                        try {
                            Class<?> classe = Class.forName(className);
                            if (classe.isAnnotationPresent(Controller.class)) {
                                controller.add(classe.getSimpleName());

                                Method[] methodes = classe.getDeclaredMethods();

                                for (Method methode : methodes) {
                                    if (methode.isAnnotationPresent(Get.class)) {
                                        Mapping map = new Mapping(className, methode.getName());
                                        String valeur = methode.getAnnotation(Get.class).value();
                                        lien.put(valeur, map);
                                    }
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
