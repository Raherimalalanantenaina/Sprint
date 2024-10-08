package mg.itu.prom16.controllers;

import mg.itu.prom16.annotations.*;
import mg.itu.prom16.models.ModelView;
import mg.itu.prom16.session.CustomSession;
import mg.itu.prom16.map.Mapping;
import mg.itu.prom16.map.VerbAction;
import mg.itu.prom16.session.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private List<String> controller = new ArrayList<>();
    private String controllerPackage;
    boolean checked = false;
    HashMap<String, Mapping> lien = new HashMap<>();
    String error = "";

    @Override
    public void init() throws ServletException {
        super.init();
        controllerPackage = getInitParameter("controller-package");
        try {
            this.scan();
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String[] requestUrlSplitted = request.getRequestURL().toString().split("/");
        String controllerSearched = requestUrlSplitted[requestUrlSplitted.length - 1];

        response.setContentType("text/html");
        if (!error.isEmpty()) {
            out.println(error);
        } else if (!lien.containsKey(controllerSearched)) {
            out.println("<p>Méthode non trouvée.</p>");
        } else {
            try {
                Mapping mapping = lien.get(controllerSearched);
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object object = clazz.getDeclaredConstructor().newInstance();
                Method method = null;

                if (!mapping.isVerbPresent(request.getMethod())) {
                    out.println("<p>Le verbe HTTP utilisé n'est pas pris en charge pour cette action.</p>");
                }

                for (Method m : clazz.getDeclaredMethods()) {
                    for (VerbAction action : mapping.getVerb()) {
                        if (m.getName().equals(action.getAction()) && action.getVerb().equalsIgnoreCase(request.getMethod())) {
                     
                            method = m;
                            break; 
                        }
                    }
                    if (method != null) {
                        break;
                    }
                    
                }

                if (method == null) {
                    out.println("<p>Aucune méthode correspondante trouvée.</p>");
                    return;
                }

                // Injecter les paramètres dans la méthode
                Object[] parameters = getMethodParameters(method, request);

                
                Object returnValue = method.invoke(object, parameters);

                // Gestion de l'API REST
                if (method.isAnnotationPresent(RestApi.class)) {
                    response.setContentType("application/json");
                    Gson gson = new Gson();
                    if (returnValue instanceof String) {
                        String jsonResponse = gson.toJson(returnValue);
                        out.println(jsonResponse);
                    } else if (returnValue instanceof ModelView) {
                        ModelView modelView = (ModelView) returnValue;
                        String jsonResponse = gson.toJson(modelView.getData());
                        out.println(jsonResponse);
                    } else {
                        out.println("Type de données non reconnu");
                    }
                } else if (returnValue instanceof String) {
                    out.println("Méthode trouvée dans " + returnValue);
                } else if (returnValue instanceof ModelView) {
                    ModelView modelView = (ModelView) returnValue;
                    for (Map.Entry<String, Object> entry : modelView.getData().entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                    RequestDispatcher dispatcher = request.getRequestDispatcher(modelView.getUrl());
                    dispatcher.forward(request, response);
                } else {
                    out.println("Type de données non reconnu");
                }
            } catch (Exception e) {
                out.println(e.getMessage());
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

    public void scan() throws Exception {
        try {
            String classesPath = getServletContext().getRealPath("/WEB-INF/classes");
            String decodedPath = URLDecoder.decode(classesPath, "UTF-8");
            String packagePath = decodedPath + "\\" + controllerPackage.replace('.', '\\');
            File packageDirectory = new File(packagePath);
            if (!packageDirectory.exists() || !packageDirectory.isDirectory()) {
                throw new Exception("Package n'existe pas");
            } else {
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

                                for (Method method : methodes) {
                                    if (method.isAnnotationPresent(UrlAnnotation.class)) {
                                        UrlAnnotation urlAnnotation = method.getAnnotation(UrlAnnotation.class);
                                        String url = urlAnnotation.value();
                                        String verb = "GET"; 
                                        if (method.isAnnotationPresent(Get.class)) {
                                            verb = "GET";
                                        } else if (method.isAnnotationPresent(Post.class)) {
                                            verb = "POST";
                                        }
                                        VerbAction verbAction = new VerbAction(verb,method.getName());
                                        Mapping map = new Mapping(className);
                                        if (lien.containsKey(url)) {
                                            Mapping existingMap = lien.get(url);
                                            if (existingMap.getVerb().contains(verbAction)) {
                                                throw new Exception("Duplicate URL: " + url);
                                            } else {
                                                existingMap.setVerbActions(verbAction);
                                            }
                                        } else {
                                            map.setVerbActions(verbAction);
                                            lien.put(url, map);
                                        }
                                        
                                    }else{
                                        throw new Exception("il faut avoir une annotation url dans le controlleur  "+ className);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            throw e;
                        }

                    }
                } else {
                    throw new Exception("le package est vide");
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private Object[] getMethodParameters(Method method, HttpServletRequest request) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            try {
                if (parameters[i].isAnnotationPresent(Param.class)) {
                    Param param = parameters[i].getAnnotation(Param.class);
                    String paramValue = request.getParameter(param.value());

                    // Vérifiez si le paramètre est nul ou vide
                    if (param == null) {
                        throw new Exception("ETU2777 Le paramètre " + param.value() + " est manquant.");
                    }

                    parameterValues[i] = paramValue; // En supposant que tous les paramètres sont des chaînes pour simplifier
                }
                if (parameters[i].isAnnotationPresent(RequestObject.class)) {
                    parameterValues[i] = RequestMapper.mapRequestToObject(request, parameters[i].getType());
                }
                if (parameters[i].getType().equals(CustomSession.class)) {
                    CustomSession session = new CustomSession(request.getSession());
                    parameterValues[i] = session;
                }
            } catch (Exception e) {
                throw e;
            }
        }

        return parameterValues;
    }
}
