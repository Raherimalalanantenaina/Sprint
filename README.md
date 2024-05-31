*** SPRINT 1 ***
-> Utilisation du front-controller.jar dans le projet de test:
    - Copier le fichier .jar dans le lib du projet de test
    - Quand on cree un controller dans le projet, on doit l'annoter par l'annotation dans le framework
    qui se nomme Controller
    - Apres dans le fichier web.xml de notre projet, il faut ajouter un init-param pour indiquer au framework ou sont les controleurs qu'on a annoté. mettre comme param-name "controller-package" et comme param-value le package des controleurs
    

*** SPRINT 2 ***
-> Il faut annoter les methodes dans les controlleurs par l'annotation Get("nom-methode")