-- Insert Proficiency Levels
INSERT INTO Proficiency_Level (proficiency_level_name) VALUES 
('A1'), ('A2'), ('B1'), ('B2'), ('C1'), ('C2'), ('Native');

-- Insert Languages
INSERT INTO Language (language_name) VALUES 
('English'), ('Russian'), ('Spanish'), ('French'), ('German'),
('Chinese'), ('Japanese'), ('Arabic'), ('Portuguese'), ('Italian'),
('Korean'), ('Hindi'), ('Turkish'), ('Polish'), ('Ukrainian');

-- Insert Countries
INSERT INTO Country (country_name) VALUES
('United States'), ('United Kingdom'), ('Canada'), ('Australia'), ('Germany'),
('France'), ('Spain'), ('Italy'), ('Russia'), ('China'),
('Japan'), ('South Korea'), ('Brazil'), ('Mexico'), ('Ukraine'),
('Poland'), ('Turkey'), ('India'), ('Egypt'), ('Saudi Arabia');

-- Insert Cities with their Countries
-- United States
INSERT INTO City (city_name, Country) VALUES
('New York', 'United States'), ('Los Angeles', 'United States'), 
('Chicago', 'United States'), ('Houston', 'United States'),
('Phoenix', 'United States'), ('Philadelphia', 'United States');

-- United Kingdom
INSERT INTO City (city_name, Country) VALUES
('London', 'United Kingdom'), ('Manchester', 'United Kingdom'),
('Birmingham', 'United Kingdom'), ('Liverpool', 'United Kingdom'),
('Edinburgh', 'United Kingdom'), ('Glasgow', 'United Kingdom');

-- Canada
INSERT INTO City (city_name, Country) VALUES
('Toronto', 'Canada'), ('Vancouver', 'Canada'),
('Montreal', 'Canada'), ('Calgary', 'Canada'),
('Ottawa', 'Canada'), ('Edmonton', 'Canada');

-- Australia
INSERT INTO City (city_name, Country) VALUES
('Sydney', 'Australia'), ('Melbourne', 'Australia'),
('Brisbane', 'Australia'), ('Perth', 'Australia'),
('Adelaide', 'Australia'), ('Gold Coast', 'Australia');

-- Germany
INSERT INTO City (city_name, Country) VALUES
('Berlin', 'Germany'), ('Munich', 'Germany'),
('Hamburg', 'Germany'), ('Frankfurt', 'Germany'),
('Cologne', 'Germany'), ('Stuttgart', 'Germany');

-- France
INSERT INTO City (city_name, Country) VALUES
('Paris', 'France'), ('Marseille', 'France'),
('Lyon', 'France'), ('Toulouse', 'France'),
('Nice', 'France'), ('Nantes', 'France');

-- Spain
INSERT INTO City (city_name, Country) VALUES
('Madrid', 'Spain'), ('Barcelona', 'Spain'),
('Valencia', 'Spain'), ('Seville', 'Spain'),
('Zaragoza', 'Spain'), ('Malaga', 'Spain');

-- Italy
INSERT INTO City (city_name, Country) VALUES
('Rome', 'Italy'), ('Milan', 'Italy'),
('Naples', 'Italy'), ('Turin', 'Italy'),
('Palermo', 'Italy'), ('Genoa', 'Italy');

-- Russia
INSERT INTO City (city_name, Country) VALUES
('Moscow', 'Russia'), ('Saint Petersburg', 'Russia'),
('Novosibirsk', 'Russia'), ('Yekaterinburg', 'Russia'),
('Kazan', 'Russia'), ('Nizhny Novgorod', 'Russia');

-- China
INSERT INTO City (city_name, Country) VALUES
('Beijing', 'China'), ('Shanghai', 'China'),
('Guangzhou', 'China'), ('Shenzhen', 'China'),
('Chengdu', 'China'), ('Chongqing', 'China');

-- Japan
INSERT INTO City (city_name, Country) VALUES
('Tokyo', 'Japan'), ('Yokohama', 'Japan'),
('Osaka', 'Japan'), ('Nagoya', 'Japan'),
('Sapporo', 'Japan'), ('Fukuoka', 'Japan');

-- South Korea
INSERT INTO City (city_name, Country) VALUES
('Seoul', 'South Korea'), ('Busan', 'South Korea'),
('Incheon', 'South Korea'), ('Daegu', 'South Korea'),
('Daejeon', 'South Korea'), ('Gwangju', 'South Korea');

-- Brazil
INSERT INTO City (city_name, Country) VALUES
('São Paulo', 'Brazil'), ('Rio de Janeiro', 'Brazil'),
('Brasília', 'Brazil'), ('Salvador', 'Brazil'),
('Fortaleza', 'Brazil'), ('Belo Horizonte', 'Brazil');

-- Mexico
INSERT INTO City (city_name, Country) VALUES
('Mexico City', 'Mexico'), ('Guadalajara', 'Mexico'),
('Monterrey', 'Mexico'), ('Puebla', 'Mexico'),
('Tijuana', 'Mexico'), ('León', 'Mexico');

-- Ukraine
INSERT INTO City (city_name, Country) VALUES
('Kyiv', 'Ukraine'), ('Kharkiv', 'Ukraine'),
('Odesa', 'Ukraine'), ('Dnipro', 'Ukraine'),
('Donetsk', 'Ukraine'), ('Lviv', 'Ukraine');

-- Poland
INSERT INTO City (city_name, Country) VALUES
('Warsaw', 'Poland'), ('Kraków', 'Poland'),
('Łódź', 'Poland'), ('Wrocław', 'Poland'),
('Poznań', 'Poland'), ('Gdańsk', 'Poland');

-- Turkey
INSERT INTO City (city_name, Country) VALUES
('Istanbul', 'Turkey'), ('Ankara', 'Turkey'),
('İzmir', 'Turkey'), ('Bursa', 'Turkey'),
('Adana', 'Turkey'), ('Gaziantep', 'Turkey');

-- India
INSERT INTO City (city_name, Country) VALUES
('Mumbai', 'India'), ('Delhi', 'India'),
('Bangalore', 'India'), ('Hyderabad', 'India'),
('Chennai', 'India'), ('Kolkata', 'India');

-- Egypt
INSERT INTO City (city_name, Country) VALUES
('Cairo', 'Egypt'), ('Alexandria', 'Egypt'),
('Giza', 'Egypt'), ('Shubra El-Kheima', 'Egypt'),
('Port Said', 'Egypt'), ('Suez', 'Egypt');

-- Saudi Arabia
INSERT INTO City (city_name, Country) VALUES
('Riyadh', 'Saudi Arabia'), ('Jeddah', 'Saudi Arabia'),
('Mecca', 'Saudi Arabia'), ('Medina', 'Saudi Arabia'),
('Dammam', 'Saudi Arabia'), ('Taif', 'Saudi Arabia');